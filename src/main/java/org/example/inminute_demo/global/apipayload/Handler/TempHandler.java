package org.example.inminute_demo.global.apipayload.Handler;


import org.example.inminute_demo.global.apipayload.code.BaseCode;
import org.example.inminute_demo.global.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.global.exception.GeneralException;

public class TempHandler extends GeneralException {
    public TempHandler(BaseCode errorCode) {
        super((ErrorStatus) errorCode);
    }
}