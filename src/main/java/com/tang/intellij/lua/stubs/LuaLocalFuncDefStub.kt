/*
 * Copyright (c) 2017. tangzx(love.tangzx@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tang.intellij.lua.stubs

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.util.io.StringRef
import com.tang.intellij.lua.psi.LuaLocalFuncDef
import com.tang.intellij.lua.psi.LuaParamInfo
import com.tang.intellij.lua.psi.impl.LuaLocalFuncDefImpl
import com.tang.intellij.lua.psi.overloads
import com.tang.intellij.lua.ty.IFunSignature
import com.tang.intellij.lua.ty.ITy

class LuaLocalFuncDefElementType
    : LuaStubElementType<LuaLocalFuncDefStub, LuaLocalFuncDef>("LOCAL_FUNC_DEF") {
    override fun serialize(stub: LuaLocalFuncDefStub, stream: StubOutputStream) {
        stream.writeName(stub.name)
        stream.writeTyNullable(stub.returnDocTy)
        stream.writeParamInfoArray(stub.params)
        stream.writeSignatures(stub.overloads)
    }

    override fun shouldCreateStub(node: ASTNode): Boolean {
        val psi = node.psi as LuaLocalFuncDef
        return createStubIfParentIsStub(node) && psi.name != null
    }

    override fun createStub(def: LuaLocalFuncDef, parentStub: StubElement<*>?): LuaLocalFuncDefStub {
        val retDocTy = def.comment?.returnDef?.resolveTypeAt(0)
        val params = def.params
        val overloads = def.overloads
        return LuaLocalFuncDefStub(def.name!!,
                retDocTy,
                params,
                overloads,
                parentStub,
                this)
    }

    override fun deserialize(stream: StubInputStream, parentStub: StubElement<*>?): LuaLocalFuncDefStub {
        val name = stream.readName()
        val retDocTy = stream.readTyNullable()
        val params = stream.readParamInfoArray()
        val overloads = stream.readSignatures()
        return LuaLocalFuncDefStub(StringRef.toString(name),
                retDocTy,
                params,
                overloads,
                parentStub,
                this)
    }

    override fun indexStub(stub: LuaLocalFuncDefStub, sink: IndexSink) {

    }

    override fun createPsi(stub: LuaLocalFuncDefStub): LuaLocalFuncDef {
        return LuaLocalFuncDefImpl(stub, this)
    }
}

class LuaLocalFuncDefStub(
        val name: String,
        override val returnDocTy: ITy?,
        override val params: Array<LuaParamInfo>,
        override val overloads: Array<IFunSignature>,
        parent: StubElement<*>?,
        type: LuaStubElementType<*, *>
) : LuaStubBase<LuaLocalFuncDef>(parent, type), LuaFuncBodyOwnerStub<LuaLocalFuncDef>