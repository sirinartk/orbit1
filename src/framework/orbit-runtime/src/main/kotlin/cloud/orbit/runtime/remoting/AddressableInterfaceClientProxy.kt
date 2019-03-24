/*
 Copyright (C) 2015 - 2019 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <https://www.orbit.cloud>.
 See license in LICENSE.
 */

package cloud.orbit.runtime.remoting

import cloud.orbit.core.key.Key
import cloud.orbit.core.remoting.Addressable
import cloud.orbit.core.remoting.AddressableClass
import cloud.orbit.core.remoting.AddressableInvocation
import cloud.orbit.core.remoting.AddressableReference
import cloud.orbit.runtime.hosting.DeferredWrappers
import cloud.orbit.runtime.net.Message
import cloud.orbit.runtime.net.MessageContent
import cloud.orbit.runtime.pipeline.PipelineSystem
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class AddressableInterfaceClientProxy(
    private val pipelineSystem: PipelineSystem,
    private val interfaceClass: AddressableClass,
    private val key: Key
) : InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any {
        val addressableInvocation = AddressableInvocation(
            reference = AddressableReference(
                interfaceClass = interfaceClass,
                key = key
            ),
            method = method,
            args = args ?: arrayOf()
        )

        val msg = Message(
            content = MessageContent.RequestInvocationMessage(addressableInvocation)
        )

        val completion = pipelineSystem.pushOutbound(msg)

        return DeferredWrappers.wrapReturn(completion, method)
    }
}

class AddressableInterfaceClientProxyFactory(
    private val pipelineSystem: PipelineSystem,
    private val interfaceDefinitionDictionary: AddressableInterfaceDefinitionDictionary
) {
    fun <T : Addressable> getReference(interfaceClass: Class<T>, key: Key): T {
        val interfaceDefinition = interfaceDefinitionDictionary.getOrCreate(interfaceClass)

        val invocationHandler = AddressableInterfaceClientProxy(
            pipelineSystem = pipelineSystem,
            interfaceClass = interfaceDefinition.interfaceClass,
            key = key
        )
        val javaProxy = Proxy.newProxyInstance(
            javaClass.classLoader,
            arrayOf(interfaceClass),
            invocationHandler
        )
        @Suppress("UNCHECKED_CAST")
        return javaProxy as T
    }
}