package com.example.retry.helper

import groovy.transform.CompileStatic

@CompileStatic
enum ResponseStatus {
    SERVER_ERROR,
    CLIENT_ERROR,
    SUCCESS
}

