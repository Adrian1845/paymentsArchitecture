package com.afernber.project.exception.member;

import com.afernber.project.exception.ErrorCatalog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCatalog {
    MEMBER_NOT_FOUND(1001, HttpStatus.NOT_FOUND, "The requested member was not found"),
    INVALID_MEMBER_DATA(1002, HttpStatus.BAD_REQUEST, "The member data provided is invalid"),
    MEMBER_ALREADY_EXISTS(1003, HttpStatus.CONFLICT, "A member with this information already exists"),
    MEMBER_DELETION_FORBIDDEN(1004, HttpStatus.FORBIDDEN, "This member cannot be deleted due to active dependencies");

    private final int code;
    private final HttpStatus status;
    private final String message;

}
