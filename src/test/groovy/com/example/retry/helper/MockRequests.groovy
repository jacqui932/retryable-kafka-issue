package com.example.retry.helper

import groovy.transform.CompileStatic

import java.util.function.Supplier

import static com.example.retry.helper.ResponseStatus.*

@CompileStatic
class MockRequests<T extends MockRequest, U> {

    List<T> requests = [].asSynchronized() as List<T>

    Queue responses = [] as Queue
    Map<T, Queue> requestSpecificResponses = [:]

    Supplier<U> successfulResponse
    Supplier<U> serverError
    Supplier<U> clientError

    U process(T request) {
        this.requests.add(request)
        nextResponse(request)
    }

    void addResponse(ResponseStatus response) {
        responses.add(response)
    }

    U nextResponse(T request) {
        Queue requestSpecificResponseQueue
        if (request != null) {
            requestSpecificResponseQueue = requestSpecificResponses.get(request)
        }
        if (requestSpecificResponseQueue == null || requestSpecificResponseQueue.empty) {
            if (responses.empty) {
                return successfulResponse.get()
            }
            def response = responses.poll()
            if (response == SERVER_ERROR) {
                return serverError.get()
            }
            return clientError.get()
        }
        def response = requestSpecificResponseQueue.poll()
        if (response == SERVER_ERROR) {
            return serverError.get()
        }
        return clientError.get()
    }
}
