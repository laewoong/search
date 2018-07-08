package com.laewoong.search.model.response;

/**
 * Created by laewoong on 2018. 4. 27..
 */

public enum ErrorCode {

    SE01("SE01"), // Incorrect query request (잘못된 쿼리요청입니다.)
    SE02("SE02"), // Invalid display value (부적절한 display 값입니다.)
    SE03("SE03"), // Invalid start value (부적절한 start 값입니다.)
    SE04("SE04"), // Invalid sort value (부적절한 sort 값입니다.)
    SE06("SE06"), // Malformed encoding (잘못된 형식의 인코딩입니다.)
    SE05("SE05"), // Invalid search api (존재하지 않는 검색 api 입니다.)
    SE99("SE99"), // System Error (시스템 에러)

    FAIL_NETWORK("FAIL_NETWORK"),
    ARRIVED_FINAL_RESPONSE("ARRIVED_FINAL_RESPONSE"),
    ARRIVED_EMPTY_RESPONSE("ARRIVED_EMPTY_RESPONSE"),


    /**
     * 시스템 에러가 아니라, 네이버 API 정책상 제한된 요청일 경우 아래 에러 코드를 전달하여 구체적인 예외처리 기회 제공
     */
    NAVER_MAX_START_VALUE_POLICY("NAVER_MAX_START_VALUE_POLICY");

    private String mErrorCode;

    ErrorCode(String errorCode){
        mErrorCode = errorCode;
    }

    public String getErrorCode(){
        return mErrorCode;
    }
}
