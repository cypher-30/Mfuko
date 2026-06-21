package com.chama.groupmoneymanager.ui.features.nests.members

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chama.groupmoneymanager.core.util.NestReportPdfGenerator
import com.chama.groupmoneymanager.core.util.Resource
import com.chama.groupmoneymanager.data.local.TokenManager
import com.chama.groupmoneymanager.data.local.dao.MembershipDao
import com.chama.groupmoneymanager.data.local.dao.NestDao
import com.chama.groupmoneymanager.data.local.dao.NotificationDao
import com.chama.groupmoneymanager.data.local.entities.NotificationEntity
import com.chama.groupmoneymanager.data.remote.MemberStatusDto
import com.chama.groupmoneymanager.data.repository.NestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MembersState(
    val members: List<MemberStatusDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedMemberForPayment: MemberStatusDto? = null,
    val memberPendingRemoval: MemberStatusDto? = null,
    val currentUserRole: String = "member",
    val nestId: Long = 0L,
    val nestName: String = "",
    val inviteCode: String = "",
    val showAnnouncementDialog: Boolean = false,
    val announcementSent: Boolean = false
)

@HiltViewModel
class MembersViewModel @Inject constructor(
    private val repository: NestRepository,
    private val tokenManager: TokenManager,
    private val nestDao: NestDao,
    private val membershipDao: MembershipDao,
    private val notificationDao: NotificationDao,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _state = mutableStateOf(MembersState())
    val state: State<MembersState> = _state

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    init {
        loadMembers()
    }

    fun loadMembers() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            // Read current nestId/userId directly from TokenManager (works for local tokens too).
            val nestId         = tokenManager.getCurrentNestId().first()
            val currentUserId  = tokenManager.getUserId().first()
            val nest           = nestDao.getNestById(nestId).first()

            when (val result = repository.getNestMembers(nestId)) {
                is Resource.Success -> {
                    val members = result.data ?: emptyList()
                    val role    = members.find { it.userId == currentUserId }?.role ?: "member"
                    _state.value = _state.value.copy(
                        members         = members,
                        isLoading       = false,
                        currentUserRole = role,
                        nestId          = nestId,
                        nestName        = nest?.name ?: "",
                        inviteCode      = nest?.inviteCode ?: ""
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading  = false,
                        error      = result.message,
                        nestId     = nestId,
                        nestName   = nest?.name ?: "",
                        inviteCode = nest?.inviteCode ?: ""
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun onRecordPaymentClick(member: MemberStatusDto) {
        _state.value = _state.value.copy(selectedMemberForPayment = member)
    }

    fun onDismissDialog() {
        _state.value = _state.value.copy(selectedMemberForPayment = null)
    }

    fun onRemoveMemberClick(member: MemberStatusDto) {
        _state.value = _state.value.copy(memberPendingRemoval = member)
    }

    fun onDismissRemoveDialog() {
        _state.value = _state.value.copy(memberPendingRemoval = null)
    }

    fun confirmRemoveMember() {
        val member = _state.value.memberPendingRemoval ?: return
        viewModelScope.launch {
            membershipDao.deleteMembership(member.userId, _state.value.nestId)
            _state.value = _state.value.copy(memberPendingRemoval = null)
            loadMembers()
        }
    }

    fun onExportReportClick() {
        viewModelScope.launch {
            val uri = NestReportPdfGenerator.generate(appContext, _state.value.nestName, _state.value.members)
            _eventFlow.send(UiEvent.ShareReport(uri))
        }
    }

    fun onAnnouncementIconClick() {
        _state.value = _state.value.copy(showAnnouncementDialog = true, announcementSent = false)
    }

    fun onDismissAnnouncementDialog() {
        _state.value = _state.value.copy(showAnnouncementDialog = false)
    }

    fun sendAnnouncement(message: String) {
        viewModelScope.launch {
            val nestId = _state.value.nestId
            membershipDao.getMembersOfNest(nestId).first().forEach { membership ->
                notificationDao.insertNotification(
                    NotificationEntity(
                        userId = membership.userId,
                        nestId = nestId,
                        type = "announcement",
                        message = message
                    )
                )
            }
            _state.value = _state.value.copy(showAnnouncementDialog = false, announcementSent = true)
        }
    }

    sealed class UiEvent {
        data class ShareReport(val uri: Uri) : UiEvent()
    }
}
