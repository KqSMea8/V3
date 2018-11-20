package com.huanglong.v3.netutils;

/**
 * Created by bin on 2018/1/15.
 * 网络请求api
 */

public interface Api {

    String base_api = "https://www.huachenedu.cn/v3/index.php/api/";

    //K歌分享地址
    String share_k_song_url = "https://www.huachenedu.cn/v3/h5/index.html?id=ID&uid=Uid";
    //K歌分享地址
    String share_video_url = "https://www.huachenedu.cn/v3/h5/video/index.html?id=ID";
    //社圈分享
    String share_qun = "https://www.huachenedu.cn/v3/h5/quan.html?id=ID";
    //用户注册协议
    String agreement = "https://www.huachenedu.cn/v3/h5/agreement.html";
    //"https://weisan.oss-cn-beijing.aliyuncs.com/TXUgcSDK.licence"
    //短视频licenceKey
    String licenceKey = "e7931fa6b4b277002c761c11ad1fecb3";
    //短视频licence下载地址
    String licenceUrl = "http://license.vod2.myqcloud.com/license/v1/ce749e824851c2fb5e34f3942e6319de/TXUgcSDK.licence";
    //登录
    String login = "base/login";
    //注册获取验证码
    String get_code_register = "sms/reg_verifycode";
    //忘记密码获取验证码
    String get_code_forget = "sms/getpwd_verifycode";
    //注册
    String register = "base/register";
    //忘记密码
    String forget_password = "base/setpwd";
    //联系人
    String contaces = "base/choicecontact";
    //职业分类接口
    String job_class = "user/career_cate";
    //根据职业分类筛选用户列表接口
    String getMemberListByCarrer = "user/getMemberListByCarrer";
    //添加好友（搜索和好友详情）
    String search_user = "Friend/search_user";
    //友圈列表接口
    String quan = "quan/list";
    //上传图片
    String uploadimg = "Quan/uploadimg";
    //发布（友圈和社圈）
    String add_quan = "Quan/add";
    //获取好友关系接口
    String queryfriendlyrelation = "User/queryfriendlyrelation";
    //发起群聊（选择好友创建群）
    String queryqunfriends = "User/queryqunfriends";
    //福包列表接口
    String fubao_list = "fubao/list";
    //福包详情接口 中奖人员列表
    String zhongjiang_list = "fubao/zhongjiang_list";
    //拆福包（输入口令）
    String open_redpackage = "fubao/open_redpackage";
    //支付
    String pay = "Wxpay/pay";
    //发布福包接口
    String fubao_create = "fubao/create";
    //黄页列表接口
    String huangye_list = "huangye/list";
    //黄页详情接口
    String huangye_detail = "huangye/detail";
    //直播分类接口
    String live_cate_list = "liveplay/live_cate_list";
    //直播列表接口
    String liveplay_list = "liveplay/list";
    //实名认证：（直播前要先实名认证）
    String userCertificate = "user/userCertificate";
    //判断是否已经实名认证接口：
    String getCheckUser = "user/getCheckUser";
    //个人资料
    String getUserInfo = "user/getUserInfo";
    //编辑个人资料接口
    String updateUserInfo = "user/updateUserInfo";
    //创建直播活动接口（生成推流和拉流地址）
    String createLive = "liveplay/createLive";
    //音频分类接口
    String book_cate_list = "book/book_cate_list";
    //有声书列表
    String book_list = "book/list";
    //K歌关注列表
    String music_follow_list = "music/follow_list";
    //k歌附近列表接口
    String music_location_list = "music/location_list";
    //k歌热门列表接口
    String music_hot_list = "music/hot_list";
    //视频分类
    String video_video_cate_list = "video/video_cate_list";
    //视频关注列表接口
    String video_follow_list = "video/follow_list";
    //视频推荐接口
    String video_recommend_list = "video/recommend_list";
    //视频附近列表接口
    String video_location_list = "video/location_list";
    //音频详情接口（节目列表）
    String book_program = "book/program";
    //k歌详情接口（个人资料、关注、礼物榜、评论列表、其他作品）
    String music_detail = "music/detail";
    //k歌评论列表
    String music_comment_list = "music/comment_list";
    //1.广告接口 position：1-活动；2-主页广告；
    String banner_index = "banner/index";
    //个人资料接口（基本信息和自媒体介绍）
    String user_getUserInfo = "user/getUserInfo";
    //g个人主页
    String user_getHomepage = "user/getHomepage";
    //更新用户位置接口
    String user_updateUserLocation = "user/updateUserLocation";
    //点赞
    String quan_add_blog_upvote = "quan/add_blog_upvote";
    //发布评论接口
    String blog_add_comment = "quan/add_comment";
    //回复评论
    String blog_reply_blog = "quan/reply_blog";
    //评论删除
    String quan_delete_comment = "quan/delete_comment";
    //社圈分类
    String Quan_cate_list = "Quan/cate_list";
    //活动列表
    String activity_list = "activity/list";
    //群搜索
    String search_group = "user/search_group";
    //加群验证
    String judy_group = "user/judy_group";
    //活动详情接口
    String activity_detail = "activity/detail";
    //活动报名：
    String activity_enroll = "activity/enroll";
    //主页接口
    String homepage_recommend = "homepage/homepage_recommend";
    //关注
    String user_add_zan = "user/add_zan";
    //我的主题分类接口
    String member_subject_category = "user/member_subject_category";
    //根据分类查看主题图片展示接口
    String member_subject_show = "user/member_subject_show";
    //我的直播
    String mylive_recommend = "user/mylive_recommend";
    //我的视频接口
    String my_video_recommend = "user/my_video_recommend";
    //我的k歌
    String mymusic_recommend = "user/mymusic_recommend";
    //我的动态（朋友圈）
    String myblog_recommend = "user/myblog_recommend";
    //直播间创建群
    String live_qun = "liveplay/live_qun";
    //直播间点赞接口
    String liveplay_add_upvote = "liveplay/add_upvote";
    //直播关注接口
    String liveplay_add_guanzhu = "liveplay/add_guanzhu";
    //直播结束的统计接口
    String liveplay_analysize = "liveplay/analysize";
    //头像列表接口
    String live_avatar_list = "liveplay/live_avatar_list";
    //我的音频（故事书)
    String my_book_recommend = "user/my_book_recommend";
    //主题分类添加
    String user_add_subject = "user/add_subject";
    //添加主题分类图片接口
    String user_add_subject_pic = "user/add_subject_pic";
    //充值接口
    String Wxpay_pay = "Wxpay/pay";
    //提现明细接口
    String user_withdraw = "user/withdraw";
    //企业注册行业分类接口
    String industry_category = "user/industry_category";
    //素材分类接口、 测试地
    String video_cate_list = "video/cate_list";
    //素材热门歌曲（默认取15条点击量高的）
    String sucai_hot_list = "video/sucai_hot_list";
    //素材歌曲列表（+搜索）接口
    String song_list = "video/song_list";
    //获取发布小视频的签名
    String generate_signatrue = "video/generate_signatrue";
    //发布视频
    String video_publish = "video/publish";
    //我的通讯录
    String addressbook_list = "user/addressbook_list";
    //点赞接口
    String add_upvote = "video/add_upvote";
    //视频评论列表
    String video_comment_list = "video/comment_list";
    //发布评论接口
    String video_add_comment = "video/add_comment";
    //视频关注发布者
    String video_add_follow = "video/add_follow";
    //素材音效列表（+搜索）接口
    String book_song_list = "book/song_list";
    //音效添加分类
    String book_add_category = "book/add_category";
    //音效类目
    String book_first_cate_list = "book/first_cate_list";
    //音频上传接口
    String book_publish = "book/publish";
    //素材列表接口+搜索
    String music_song_list = "music/song_list";
    //K歌热门歌曲
    String music_sucai_hot_list = "music/sucai_hot_list";
    //发布k歌
    String music_publish = "music/publish";
    //K歌发布评论
    String music_add_comment = "music/add_comment";
    //删除友圈
    String delete_blog = "quan/delete_blog";
    //加关注接口 测试地
    String music_add_follow = "music/add_follow";
    //k歌其他作品列表
    String music_zuopin_list = "music/zuopin_list";
    //我的k歌删除
    String user_music_delete = "user/music_delete";
    //我的直播删除
    String user_live_delete = "user/live_delete";
    //我的视频删除
    String user_video_delete = "user/video_delete";
    //我的音频删除
    String user_book_delete = "user/book_delete";
    //k歌点赞接口
    String music_music_upvote = "music/music_upvote";
    //小视频评论点赞接口
    String video_add_comment_upvote = "video/add_comment_upvote";
    //直播礼物列表接口
    String liveplay_gift_list = "liveplay/gift_list";
    // 送礼物（余额处理-消费）接口
    String liveplay_send_gift = "liveplay/send_gift";
    //临时消息列表
    String user_temporary_friend_list = "user/temporary_friend_list";
    //发临时消息
    String user_temporary_friend_add_chat = "user/temporary_friend_add_chat";
    //找公司信息-黄页分类
    String huangye_huangye_cate_list = "huangye/huangye_cate_list";
    //黄页-找信息列表
    String huangye_huangye_list = "huangye/huangye_list";
    //发布黄页信息-找信息接口
    String huangye_publish = "huangye/publish";
    //我的-社圈列表
    String user_myblog_shequan_recommend = "user/myblog_shequan_recommend";
    //我的-意见反馈
    String user_my_suggest = "user/my_suggest";
    //删除主题图片
    String user_delete_subject_pic = "user/delete_subject_pic";
    //修改主题分类
    String edit_subject = "user/edit_subject";
    //删除主题分类
    String delete_subject = "user/delete_subject";
    //素材分类接口、 测试地
    String music_cate_list = "music/cate_list";
    //礼物榜更多列表
    String music_gift_list = "music/gift_list";
    //找信息详情-评论列表
    String huangye_comment_list = "huangye/comment_list";
    //发布评论接口
    String huangye_add_comment = "huangye/add_comment";
    //我的关注列表
    String user_my_guanzhu = "user/my_guanzhu";
    //K歌详情的赞列表
    String music_zan_list = "music/zan_list";
    //企业发友圈权限
    String user_limit_send = "user/limit_send";
    //音频点击量
    String book_anlysize_program = "book/anlysize_program";
    //音频评论列表
    String book_comment_list = "book/comment_list";
    //10.音频赞列表接口
    String book_zan_list = "book/zan_list";
    //赞接口
    String book_book_upvote = "book/book_upvote";
    //发布评论接口
    String book_add_comment = "book/add_comment";
    //分享统计
    String music_add_share = "music/add_share";
    //视频统计
    String video_add_share = "video/add_share";
    //我的礼物
    String user_my_gift = "user/my_gift";
    //临时聊天记录
    String user_short_message_list = "user/short_message_list";
    //临时会话未读书
    String user_message_count = "user/message_count";
    //删除临时聊天消息
    String user_friend_dailog_delete = "user/friend_dailog_delete";
    //微信小程序申请接口
    String user_apply_program = "user/apply_program";
    //微信小程序分类接口
    String user_wx_cate_list = "user/wx_cate_list";
    //获取微信小程序列表接口
    String user_getProgramList = "user/getProgramList";

}
