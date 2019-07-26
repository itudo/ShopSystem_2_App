package com.enjoyshop.contants;

/**
 * <pre>
 *     version: 1.0
 * </pre>
 */


public interface HttpContants {
    public static final String BASE_URI = "http://192.168.43.53:8762/service-user/";
    //public static final String BASE_URI = "http://192.168.30.13:8762/service-user/";

    public static final String BASE_URI_ADMIN = "http://192.168.43.53:8762/service-admin/";

    public static final String BASE_URI_GOODS = BASE_URI_ADMIN+"imgGoods/";
    public static final String BASE_URI_HEAD = BASE_URI+"imgHead/";
    public static final String BASE_URI_REVIEW = BASE_URI+"imgReview/";

    public static final String BASE_URL = "http://112.124.22.238:8081/course_api/";       //url的基类

    public static final String HOME_BANNER_URL = BASE_URL + "banner/query";              //首页轮播图url

    public static final String HOME_CAMPAIGN_URL = BASE_URL + "campaign/recommend";      //首页商品信息url

    public static final String HOT_WARES = BASE_URL + "wares/hot";
    //热卖fragment 数据

    public static final String CATEGORY_LIST = BASE_URL + "category/list";
    //分类一级菜单

    public static final String WARES_LIST = BASE_URL + "wares/list";          //分类二级菜单

    public static final String LOGIN = BASE_URL + "auth/login";                  //登录

    public static final String USER_DETAIL = BASE_URL + "user/get?id=1";

    public static final String REG = BASE_URL + "auth/reg";                  //注册

    public static final String ORDER_CREATE = BASE_URL + "/order/create";        //提交订单

    public static final String requestWeather = "http://apicloud.mob.com/v1/weather/query";
    //mob查询天气的接口

    public static final String WARES_CAMPAIN_LIST = BASE_URL + "wares/campaign/list";
    //热门商品下的商品列表

    public static final String ADDRESS_LIST = BASE_URL + "addr/list";           //收货地址列表
    public static final String ADDRESS_CREATE = BASE_URL + "addr/create";         //创建新的地址
    public static final String ADDRESS_UPDATE = BASE_URL + "addr/update";          //更新新的地址

    public static final String WARES_DETAIL = BASE_URI + "detail.html";        //商品详情图文详情

    public static final String FAVORITE_LIST = BASE_URL + "favorite/list";
    public static final String FAVORITE_CREATE = BASE_URL + "favorite/create";


    public static final String THIRD_TYPE = BASE_URI + "findAllThirdType.action";  //查询分类
    public static final String USER_VALIATE = BASE_URI + "userValiate.action";     //校验信息
    public static final String USER_REG = BASE_URI + "userReg.action";            //用户注册
    public static final String USER_LOGIN = BASE_URI + "login.action";            //用户登录
    public static final String GET_ADDRESS = BASE_URI + "getAddress.action";      //获取地址信息
    public static final String DEL_ADDRESS = BASE_URI + "delAddress.action";      //删除地址
    public static final String DEFAULT_ADDRESS = BASE_URI + "setDefaultAddress.action";//默认地址修改
    public static final String ADD_ADDRESS = BASE_URI + "addAddress.action";      //地址添加
    public static final String UPDATE_ADDRESS = BASE_URI + "updateAddress.action";  //地址修改
    public static final String GOODS_LIST = BASE_URI + "findGoodsList.action";     //查询商品信息
    public static final String GOODS_DETAIL = BASE_URI + "goodsdetail.action";     //查询商品信息
    public static final String REVIEW_LIST = BASE_URI + "reviewList.action";     //查询评论信息
    public static final String CART_LIST = BASE_URI + "getCart.action";     //查询购物车信息
    public static final String ADD_CART = BASE_URI + "addCart.action";     //加入购物车
    public static final String UPDATE_CART = BASE_URI + "updateCartDetailCount.action";     //修改购物车数量
    public static final String DELETE_CART = BASE_URI + "delCartDetail.action";     //删除购物车
    public static final String GET_ORDERS = BASE_URI + "showOrder.action";     //获取订单信息
    public static final String GET_DEFAULT = BASE_URI + "getDefaultAddress.action";     //获取默认地址
    public static final String INSERT_ORDER_CART = BASE_URI + "insertOrderFromCart.action";     //从购物车添加订单
    public static final String INSERT_ORDER = BASE_URI + "insertOrder.action";     ///从界面添加订单
    public static final String GET_STATUS = BASE_URI + "getStatus.action";     ///查看订单状态
    public static final String CHANGE_ORDER = BASE_URI + "changeOrder.action";     ///更改订单状态
    public static final String PAY_ORDER = BASE_URI + "pay.action";     ///支付
    public static final String GET_PAY_URL = BASE_URI + "payUrl.action";     ///获取付款二维码
    public static final String UPDATE_USER = BASE_URI + "updateUser.action";     ///修改个人信息
    public static final String UPDATE_USER_HEAD = BASE_URI + "uploadHead.action";     ///修改头像
    public static final String CHANGE_PWD = BASE_URI + "updatepwd.action";     ///修改密码
    public static final String ADD_REVIEW = BASE_URI + "insertReview.action";     ///添加评价
    public static final String COLLECT_GOODS = BASE_URI + "addCollect.action";     ///收藏商品
    public static final String GET_COLLECT_GOODS = BASE_URI + "getCollect.action";     ///获取收藏商品
    public static final String SEARCH_GOODS = BASE_URI + "search2.action";     ///搜索商品
    public static final String SEND_MAIL = BASE_URI + "sendMail.action";     ///发送短信验证码
    public static final String YANZHENG = BASE_URI + "forget1.action";     ///验证身份
    public static final String SEND_EMAIL = BASE_URI + "sendEmail.action";     ///发送邮箱验证码
}
