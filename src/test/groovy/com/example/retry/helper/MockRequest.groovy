package com.example.retry.helper

abstract class MockRequest<T extends MockRequest> {

    static def assertJsonEquals(String expected, String actual) {
        tryAssertion({ assertEquals(expected, actual, false) })
    }

    static def tryAssertion(Closure<Void> assertion) {
        try {
            assertion.run()
            return true
        } catch (AssertionError e) {
            return false
        }
    }

    protected abstract boolean matches(T request);
}
