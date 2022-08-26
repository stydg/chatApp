package com.example.ChatApp.network;



public interface API {
    //Login
    String apiNotificationKey = "websocket.apiNotificationKey";

    //Common
    String selectProfile = "websocket.selectProfile";
    String apiPostUrl = "websocket.apiPostUrl";
    String selectMessageAction = "websocket.selectMessageAction";
    String selectCommandList = "websocket.selectCommandList";
    String command = "local.command";
    //Hi-Feedback 전용 API 호출처리 - 2021.6.9
    String hfcommand = "websocket.apiMobileHyFeedBack";
    String apiImageByte = "websocket.apiImageByte";
    String apiRequestAuthKey = "websocket.apiRequestAuthKey";
    String searchAllUserList = "bizrunner.SearchUserList";
    String searchDeptList = "bizrunner.SearchDeptList";
    String selectEmoticonList = "websocket.selectEmoticonList";
    String selectEmoticonGroup = "websocket.selectEmoticonGroup";
    String selectEmoticonByGropID = "websocket.selectEmoticonByGropID";
    String apiAuthSecurity = "websocket.apiAuthSecurity";
    String selectProfileSecurity = "websocket.selectProfileSecurity";
    String apiAuthAppSecurity = "websocket.apiAuthAppSecurity";
    //이모티콘 자주사용 기능(즐겨찾기) 추가 - 2021.5.17
    String addLikeEmoticon = "websocket.addLikeEmoticon";
    String delLikeEmoticon = "websocket.delLikeEmoticon";
    String selectAddLikeEmoticon = "websocket.selectAddLikeEmoticon";
    //Channel
    String selectChannelList = "bizrunner.selectChannelList";
    //hyTUBE 채널 분리 - 2020.10.30
//    String selectHyTUBEChannelList = "bizrunner.selectHyTUBEChannelList";
    //하이피드백 채널 분리 - 2021.4.21
//    String selectHFchannelList = "bizrunner.selectHyFeedBackChannelList";

    String selectDMChannel = "websocket.selectDMChannel";
    //DM 즐겨찾기 탭처리 - 2020.11.16
    String selectDMFavoriteChannel = "websocket.SelectDMFavoriteChannel";
    String searchChannelList = "websocket.searchChannelList";
    String createChannel = "websocket.createChannel";
    String insertDMChannel = "websocket.insertDMChannel";
    String apiSearchChannelList = "websocket.apiSearchChannelList";
    String apiSearchUserList = "websocket.apiSearchUserList";
    String selectDMFavoriteMembers = "websocket.selectDMFavoriteMembers";
    String apiChannelJoinInvite = "websocket.apiChannelJoinInvite";
    String apiChannelUpdate = "websocket.apiChannelUpdate";
    String updateDMChannel = "websocket.updateDMChannel";
    String updateChannelAlias = "websocket.updateChannelAlias";
    String updateDMChannelAlias = "websocket.updateDMChannelAlias";

    //익명채널 멤버 추가 구조 개선(모바일 적용 안함) - 2020.6.8
    String updatechannelmemberadd = "websocket.updatechannelmemberadd";

    //추천리스트 조회 기능추가- 2020.9.25(적용안하기로함!)
    String selectRecommanders = "websocket.selectDMFavoriteMembers";

    //Unread
    String unreadChannelMessageCount = "websocket.unreadChannelMessageCount";
    String unreadDMChannelMessageCount = "websocket.unreadDMChannelMessageCount";
    String unreadChannelMessageList = "websocket.unreadChannelMessageMobile";
    String unreadDMChannelMessageList = "websocket.unreadDMChannelMessage";

    //hyTUBE 채널 분리 - 2020.10.30
//    String unreadHytubeMessageCount = "websocket.unreadHytubeMessageCount";
//    String unreadHytubeMessageList = "websocket.unreadHytubeMessageMobile";

    //Channel Sub
    String selectChannelInfoSummary = "websocket.selectChannelInfoSummary";
    String editPLChannel = "websocket.editPLChannel";
    String selectChannelInMemberNewJoin = "websocket.selectChannelInMemberNewJoin";
    String selectPinnedMessageList = "websocket.selectPinnedMessageList";
    String selectChannelInMember = "websocket.selectChannelInMember";
    String selectDMChannelInMember = "websocket.selectDMChannelInMember";
    String updateJoinLeave = "websocket.updateJoinLeave";
    String updateDMChannelJoinLeave = "websocket.updateDMChannelJoinLeave";
    String updateKickMember = "websocket.updateKickMember";
    String selectMemberProfile = "websocket.selectMemberProfile";
    String selectPinnedMessage = "websocket.selectPinnedMessage";
    String selectChannelInPostList = "websocket.selectChannelInPostList";
    String selectAttachFileList = "websocket.selectAttachFileList";
    String getMentionList = "websocket.getMentionList";
    String selectChannelTodoList = "websocket.selectChannelTodoList";
    String updateTodoTask = "websocket.updateTodoTask";
    String removeTodoTask = "websocket.removeTodoTask";
    String selectTodoListByMessageID = "websocket.selectTodoListByMessageID";
    String selectOnVotingList = "websocket.selectOnVotingList";
    String searchVote = "websocket.searchVote";
    String addVoteMessage = "websocket.addVoteMessage";
    String updateVoteMessage = "websocket.updateVoteMessage";
    String attendVote = "websocket.attendVote";
    String selectVoteByMessageID = "websocket.selectVoteByMessageID";
    String addVoteAnswer = "websocket.addVoteAnswer";
    String requestChannelDelete = "websocket.requestChannelDelete";
    String rejectChannelDelete = "websocket.rejectChannelDelete";
    String requestChannelDeleteAdmin = "websocket.requestChannelDeleteAdmin";

    //토론리스트 추가 -2020.8.24
    String searchDebate = "websocket.selectDebateList";

    //채널 공지 개선 - 2020.12.3(채널 공지 펼침 여부 추가)
    String channelNoticeSetting = "websocket.channelNoticeSetting";

    //Message
    String selectMessageList = "websocket.selectMessageList";
    //String searchMessageList = BuildConfig.searchMessageList;
    String addMessage = "websocket.addMessage";
    String selectMessage = "websocket.selectMessage";
    String updateMessage = "websocket.updateMessage";
    String deleteMessage = "websocket.removeMessage";
    String addComment = "websocket.addComment";
    String updateComment = "websocket.updateComment";
    String deleteComment = "websocket.removeComment";
    String apigetpost = "websocket.apigetpost";
    String addPost = "websocket.apiaddpost";
    String updatePost = "websocket.apiupdatepost";
    String updatePinned = "websocket.updatePinned";
    String updateFavoriteChannel = "websocket.updateFavoriteChannel";
    String addFavorite = "websocket.addFavorite";
    String removeFavorite = "websocket.removeFavorite";
    String copyMessage = "websocket.copyMessage";
    String richNotificationResponse = "websocket.apiRichNotificationResponse";
    String richNotificationGetimageInfo = "websocket.getimageinfo";

    String searchMessageListDetail = "websocket.searchMessageListDetail";
    String selectPostContent = "bizrunner.selectPostContent";

    String selectWritingMessageProfileNoticeStart = "websocket.selectWritingMessageProfileNoticeStart";
    String selectWritingMessageProfileNoticeStop = "websocket.selectWritingMessageProfileNoticeStop";

    //Notice
    String noticeAPI = "websocket.apirequest";
    String selectAlarmCenterList = "websocket.selectAlarmCenterList";
    String updateAlarmCenter = "websocket.updateAlarmCenter";
    String deleteAlarmCenter = "websocket.deleteAlarmCenter";
    String searchAlarmCenterList = "websocket.searchAlarmCenterList";
    String apiGetMobileConfirm = "websocket.apiGetMobileConfirm";
    String apiProcessMobileConfirm = "websocket.apiProcessMobileConfirm";

    //Settings
    String selectPrivateAlarmSetting = "websocket.selectPrivateAlarmSetting";
    String updatePrivateAlarmSetting = "websocket.updatePrivateAlarmSetting";
    String channelAlarmSetting = "websocket.channelAlarmSetting";
    String updateUserLanguageType = "websocket.updateUserLanguageType";

    //용어집
    String selectDictionary = "websocket.selectDictionary";

    //채널 순서 변경
    String insertPLChannel = "websocket.insertPLChannel";
    String updateChannelList = "websocket.api.updateChannelList";

    String selectAllNickIcon = "websocket.selectAllNickIcon";
    String updateNickName = "websocket.updateNickName";

    // 봇 목록
    String selectBotTypeList = "websocket.selectBotTypeList";

    // 추천
    String addLike = "websocket.addLike";

    //용어사전
    String termDictionary = "websocket.termDictionary";

    //번역
    String translation = "websocket.translation";
    String selectTranslatSupportTypeList = "websocket.selectTranslatSupportTypeList";

    //로그
    String apiMobileLog = "websocket.apiMobileLog";

    // 봇 메시지
    String addBotMessage = "websocket.addBotMessage";

    //봇인사
    String addBotGreetingMessage = "websocket.addBotGreetingMessage";

    // 익명 강퇴
    String updateKickMemberAnonymous = "websocket.updateKickMemberAnonymous";

    // 폴더(그룹) Unread Count 초기화
    String unreadMessageRead = "websocket.unreadMessageRead";

    // 채널 입력 제한
    String channelFreezingSetting = "websocket.updateFreezingAnonymous";

    // DM채널 강퇴
    String leaveDMKickMember = "websocket.leaveDMKickMember";

    //Bot 커맨드 리스트 조회
    String selectBotCommandList = "websocket.selectBotCommandList";

    //사원서비스센터
    String selectEmpServicesList ="websocket.selectEmpServicesList";

    //비상대피 건물
    String selectEmergencyMobileBuildList = "websocket.selectEmergencyMobileBuildList";
    //비상대피 역할
    String selectEmergencyPositionMission = "websocket.selectEmergencyPositionMission";
    //비상대피 임무 목록
    String selectEmergencyPositionList = "websocket.selectEmergencyPositionList";

    //안전관리감독자 안내화면 추가 - 2021.4.14
    String selectSafetyPositionList = "websocket.selectSHEMobile";
}
