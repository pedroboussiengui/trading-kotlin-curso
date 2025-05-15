package org.example.infra.mediator

class Mediator {
    private val handlers = mutableMapOf<String, (Any) -> Any?>()

    fun <T : Any, R : Any> register(event: String, callback: (T) -> R) {
        handlers[event] = { data -> callback(data as T) }
    }

    fun <R : Any> notifyAll(event: String, data: Any): R? {
        val handler = handlers[event] ?: return null
        return handler(data) as? R
    }
}
