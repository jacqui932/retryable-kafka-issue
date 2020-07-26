package com.example.retry.helper

abstract class MockRequest<T extends MockRequest> {

    protected abstract boolean matches(T request);
}
