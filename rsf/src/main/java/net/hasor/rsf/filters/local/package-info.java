/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * RSF服务本地调用优先功能。
 * <p>场景：使用RSF框架发布了一个远程服务，但同时本地程序又基于RSF对该服务发起了远程调用。
 * <p>在这种场景下，本地调用优先功能会保证，所有外发的服务调用请求如果本地有服务提供者，那么远程调用改为本地调用。
 * 进而提升调用速度，而上层发起远程调用的应用并不知道真实调用来自本地。
 */
package net.hasor.rsf.filters.local;