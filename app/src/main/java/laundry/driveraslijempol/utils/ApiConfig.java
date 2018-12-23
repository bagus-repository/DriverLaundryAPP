package laundry.driveraslijempol.utils;

/**
 * Created by Bagus on 24/07/2018.
 */
public class ApiConfig {
    //Host
    public static final String HOST = "aslijempol.ardata.co.id";
    public static final String PHONE = "6287834531546";
    //Server URL
    public static final String URL = "http://"+HOST+"/api/";
    //Register URL (POST)
    public static final String URL_PICKUP_LIST = URL+"driver/get_pickup_list";
    //Register URL (POST)
    public static final String URL_DELIVERY_LIST = URL+"driver/get_deliver_list";
    //Register URL (POST)
    public static final String URL_ADD_PICKUP_DETAILS = URL+"driver/add_pickup_detail";
    //Register URL (POST)
    public static final String URL_GET_DELIVERY_DETAILS = URL+"driver/get_delivery_detail";
    //Register URL (POST)
    public static final String URL_CONFIRM_PAYMENT = URL+"driver/confirm_payment";
    //Login URL (POST)
    public static final String URL_LOGIN = URL+"driver/login";
    //Cek App Update URL (POST)
    public static final String URL_CEK_APP_UPDATE = URL+"app/update_app_driver";
    //update os_player_id
    public static final String URL_UPDATE_OS_PLAYER_ID = URL+"driver/update_os_player_id";
    //get wash list
    public static final String URL_GET_CUST_WASH_LIST = URL+"driver/cust_wash_list";
}
