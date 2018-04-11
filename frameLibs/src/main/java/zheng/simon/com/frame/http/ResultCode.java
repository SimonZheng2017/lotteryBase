
package zheng.simon.com.frame.http;

public enum ResultCode {

    /**
     * 成功 true
     */
    RESULT_OK(0x00),

    /**
     * 失败 false
     */
    RESULT_FAILED(0x1),

    /**
     * 网络连接有问题
     **/
    NETWORK_TROBLE(0x2),

    /**
     * 访问平率过高，错误码
     **/
    ACCESS_TO_MANY(0x3);


    static ResultCode mapIntToValue(final int stateInt) {
        for (ResultCode value : ResultCode.values()) {
            if (stateInt == value.getIntValue()) {
                return value;
            }
        }
        return RESULT_FAILED;
    }

    private int mIntValue;

    ResultCode(int intValue) {
        mIntValue = intValue;
    }

    int getIntValue() {
        return mIntValue;
    }


}
