### ActivityDiary
#### BindCondition
在新建或修改Activity时绑定条件(WIFI/Bluetooth/GPS),条件三选一且不可重复。
##### Methods 
* `public static int Bind(int type,int activity,Context context)`:总控函数，根据 `type` 的值选择相应处理函数
* `private static int BindWIFI(int activity,Context context)`:
* `private static int BindBluetooth(int activity,Context context)`:
* `private static int BindGPS(int activity,Context context)`:
##### Constants
* `int REQUEST_CODE = 100001`
* `int Condition_WIFI = 1`
* `int Condition_Bluetooth = 2`
* `int Condition_GPS = 3`
#### ConditionInfo
获取WIFI/GPS/Bluetooth信息，包括状态、连接、变动、ID等。
##### Class
* WIFI:控制WIFI信息 
* Bluetooth：控制蓝牙信息
* GPS：控制GPS信息
##### Methods
* `public static boolean conditionCheck(Context context,int type)`:检查 `type` 对应连接是否开启
##### Constants
