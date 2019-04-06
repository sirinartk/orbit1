/*
 Copyright (C) 2015 - 2019 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <https://www.orbit.cloud>.
 See license in LICENSE.
 */

package cloud.orbit.runtime.special.capabilities.noconcrete

import cloud.orbit.core.annotation.NonConcrete
import cloud.orbit.core.remoting.Addressable

@NonConcrete
interface NoConcreteInterface : Addressable

@Suppress("UNUSED")
class NoConcreteClass : NoConcreteInterface