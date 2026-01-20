package com.afernber.project.exception.member;

import com.afernber.project.exception.BaseProjectException;

public class MemberException extends BaseProjectException {
    public MemberException(MemberErrorCode error) { super(error); }
    public MemberException(MemberErrorCode error, String message) { super(error, message); }
}
