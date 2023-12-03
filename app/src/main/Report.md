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

#### db
##### db.ActivityDiaryContract
将类变量映射到数据库对应表属性  
DiaryActivity类: Activity相关  
Diary类: Diary相关  
DiaryImage类: Diary附加的图片  
DiaryLocation类: Diary中记录的位置  
DiaryStats类: Activity统计信息(只读)  
DiarySearchSuggestion类: 搜索建议相关


class DiaryActivity implements DiaryActivityColumns

interface DiaryActivityColumns extends DiaryActivityJoinableColumns

    _ID Activity主键  
    _DELETED

interface DiaryActivityJoinableColumns

    NAME
    COLOR
    PARENT  父类Activity的id
    X_AVG_DURATION 平均持续时间
    X_START_OF_LAST 上一个结束的Activity的开始时间
    CONNECTION 连接类型(WIFI/Bluetooth/GPS)

class Diary implements DiaryColumns, DiaryActivityJoinableColumns

interface DiaryColumns

    _ID Diary主键
    _DELETED
    ACT_ID 所属Activity的id
    START
    END
    NOTE 笔记

class DiaryImage

    ID
    _DELETED
    DIARY_ID 所属Diary的id
    URI

class DiaryLocation implements DiaryLocationColumns

interface DiaryLocationColumns extends DiaryLocationJoinableColumns

    _ID Activity主键  
    _DELETED

interface DiaryLocationJoinableColumns

    LATITUDE
    LONGITUDE
    ALTITUDE
    TIMESTAMP
    SPEED
    HACC
    VACC
    SACC

class DiaryStats

    NAME Activity名称
    COLOR
    DURATION
    PORTION 所占百分比

class DiarySearchSuggestion

    _ID 搜索建议上一次改变时的时间戳
    SUGGESTION 最近的搜索建议
    ACTION 最近的搜索行为
    _DELETED
##### db.LocalDBHelper
onCreate 创建表格并插入预设的Activity
onUpgrade 可自定义Alter语句修改数据库

activity

    _id INTEGER  
    _deleted INTEGER 
    name TEXT
    color INTEGER
    parent INTEGER  

diary

    _id INTEGER  
    _deleted INTEGER 
    act_id INTEGER   
    start INTEGER 
    'end' INTEGER
    note TEXT

diary_image

    _id INTEGER  
    _deleted INTEGER  
    diary_id INTEGER
    uri TEXT

location

    _id INTEGER  
    _deleted
    ts INTEGER
    latitude REAL
    longitude REAL
    altitude REAL
    speed INTEGER
    hacc INTEGER 
    vacc INTEGER
    sacc INTEGER 

diary_search_suggestions

    _id INTEGER
    _deleted INTEGER
    action TEXT
    suggestion TEXT
##### db.ActivityDiaryContentProvider
暂时用不到(大概
class ActivityDiaryContentProvider
Cursor query
Uri insert
int delete
int update

String searchDate

##### model.DiaryActivity
##### model.DetailViewModel

##### helpers.ActivityHelper