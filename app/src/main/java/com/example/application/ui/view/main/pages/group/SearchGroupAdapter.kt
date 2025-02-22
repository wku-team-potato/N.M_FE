import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.application.data.model.groups.response.GroupResponse
import com.example.application.databinding.ItemSearchGroupBinding
import com.example.application.ui.viewmodel.main.group.GroupViewModel

class SearchGroupAdapter(
    private var groupList: List<GroupViewModel.GroupWithProfile> = emptyList(),
    private var joinedGroupList: List<GroupViewModel.GroupWithProfile> = emptyList(),
    private val onJoinClick: (GroupResponse) -> Unit
) : RecyclerView.Adapter<SearchGroupAdapter.SearchGroupViewHolder>() {

    inner class SearchGroupViewHolder(private val binding: ItemSearchGroupBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(group: GroupViewModel.GroupWithProfile) {
            binding.apply {
                groupNameTextView.text = group.group.name
                groupDescriptionTextView.text = group.group.description
                creatorTextView.text = createSpannableText("생성자: ", group.profile.username)
                pointTextView.text = createSpannableText("생성일: ", group.group.created_at.split("T")[0])

                // 가입 여부에 따라 버튼 상태 설정
                setJoinButtonState(joinedGroupList.any { it.group.id == group.group.id }, group)
            }
        }

        private fun createSpannableText(prefix: String, content: String): Spannable {
            return SpannableString("$prefix$content").apply {
                setSpan(
                    android.text.style.RelativeSizeSpan(0.75f), // 크기 조절
                    0, prefix.length, // "생성자:" 또는 "생성일:" 적용
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        private fun setJoinButtonState(isJoined: Boolean, group: GroupViewModel.GroupWithProfile) {
            binding.joinButton.apply {
                text = if (isJoined) "가입됨" else "가입하기"
                isEnabled = !isJoined
                setOnClickListener {
                    if (!isJoined) {
                        onJoinClick(group.group)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchGroupViewHolder {
        val binding = ItemSearchGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchGroupViewHolder(binding)
    }

    override fun getItemCount(): Int = groupList.size

    override fun onBindViewHolder(holder: SearchGroupViewHolder, position: Int) {
        holder.bind(groupList[position])
    }

    fun updateData(
        newGroupList: List<GroupViewModel.GroupWithProfile>,
        newJoinedGroupList: List<GroupViewModel.GroupWithProfile>
    ) {
        Log.d("SearchGroupAdapter", "Updating data: newGroupList=$newGroupList, newJoinedGroupList=$newJoinedGroupList")

        val diffCallback = GroupDiffCallback(groupList, newGroupList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        Log.d("SearchGroupAdapter", "Updating adapter data: newGroupList=$newGroupList, newJoinedGroupList=$newJoinedGroupList")

        groupList = newGroupList
        joinedGroupList = newJoinedGroupList

        diffResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

    class GroupDiffCallback(
        private val oldList: List<GroupViewModel.GroupWithProfile>,
        private val newList: List<GroupViewModel.GroupWithProfile>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].group.id == newList[newItemPosition].group.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
