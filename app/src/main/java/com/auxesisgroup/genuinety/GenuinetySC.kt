package com.auxesisgroup.genuinety

import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.RemoteCall
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tuples.generated.Tuple6
import org.web3j.tx.Contract
import org.web3j.tx.TransactionManager
import java.math.BigInteger
import java.util.*
import java.util.concurrent.Callable

/**
 *
 * Auto generated code.
 *
 * **Do not modify!**
 *
 * Please use the [web3j command line tools](https://docs.web3j.io/command_line.html),
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * [codegen module](https://github.com/web3j/web3j/tree/master/codegen) to update.
 *
 *
 * Generated with web3j version 3.4.0.
 */
class GenuinetySC : Contract {

    val itemDetails: RemoteCall<Tuple6<BigInteger, BigInteger, String, String, String, String>>
        get() {
            val function = Function(FUNC_GETITEMDETAILS,
                    Arrays.asList(),
                    Arrays.asList(object : TypeReference<Uint256>() {

                    }, object : TypeReference<Uint256>() {

                    }, object : TypeReference<Utf8String>() {

                    }, object : TypeReference<Utf8String>() {

                    }, object : TypeReference<Utf8String>() {

                    }, object : TypeReference<Utf8String>() {

                    }))
            return RemoteCall(
                    Callable {
                        val results = executeCallMultipleValueReturn(function)
                        Tuple6(
                                results[0].value as BigInteger,
                                results[1].value as BigInteger,
                                results[2].value as String,
                                results[3].value as String,
                                results[4].value as String,
                                results[5].value as String)
                    })
        }

    protected constructor(contractAddress: String?, web3j: Web3j, credentials: Credentials, gasPrice: BigInteger, gasLimit: BigInteger) : super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit) {}

    protected constructor(contractAddress: String?, web3j: Web3j, transactionManager: TransactionManager, gasPrice: BigInteger, gasLimit: BigInteger) : super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit) {}

    fun setItemDetails(_itemName: String, _merchantName: String, _link: String, _details: String): RemoteCall<TransactionReceipt> {
        val function = Function(
                FUNC_SETITEMDETAILS,
                Arrays.asList<Type<*>>(org.web3j.abi.datatypes.Utf8String(_itemName),
                        org.web3j.abi.datatypes.Utf8String(_merchantName),
                        org.web3j.abi.datatypes.Utf8String(_link),
                        org.web3j.abi.datatypes.Utf8String(_details)),
                emptyList())
        return executeRemoteCallTransaction(function)
    }

    companion object {
        private val BINARY = "608060405260405160408061077e83398101806040528101908080519060200190929190805190602001909291905050508160008190555080600181905550505061072f8061004f6000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680637c81d12314610051578063904a608b14610233575b600080fd5b34801561005d57600080fd5b50610066610361565b6040518087815260200186815260200180602001806020018060200180602001858103855289818151815260200191508051906020019080838360005b838110156100be5780820151818401526020810190506100a3565b50505050905090810190601f1680156100eb5780820380516001836020036101000a031916815260200191505b50858103845288818151815260200191508051906020019080838360005b83811015610124578082015181840152602081019050610109565b50505050905090810190601f1680156101515780820380516001836020036101000a031916815260200191505b50858103835287818151815260200191508051906020019080838360005b8381101561018a57808201518184015260208101905061016f565b50505050905090810190601f1680156101b75780820380516001836020036101000a031916815260200191505b50858103825286818151815260200191508051906020019080838360005b838110156101f05780820151818401526020810190506101d5565b50505050905090810190601f16801561021d5780820380516001836020036101000a031916815260200191505b509a505050505050505050505060405180910390f35b61035f600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506105fc565b005b6000806060806060806000546001546002600360046005838054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561040d5780601f106103e25761010080835404028352916020019161040d565b820191906000526020600020905b8154815290600101906020018083116103f057829003601f168201915b50505050509350828054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104a95780601f1061047e576101008083540402835291602001916104a9565b820191906000526020600020905b81548152906001019060200180831161048c57829003601f168201915b50505050509250818054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105455780601f1061051a57610100808354040283529160200191610545565b820191906000526020600020905b81548152906001019060200180831161052857829003601f168201915b50505050509150808054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105e15780601f106105b6576101008083540402835291602001916105e1565b820191906000526020600020905b8154815290600101906020018083116105c457829003601f168201915b50505050509050955095509550955095509550909192939495565b836002908051906020019061061292919061065e565b50826003908051906020019061062992919061065e565b50816004908051906020019061064092919061065e565b50806005908051906020019061065792919061065e565b5050505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061069f57805160ff19168380011785556106cd565b828001600101855582156106cd579182015b828111156106cc5782518255916020019190600101906106b1565b5b5090506106da91906106de565b5090565b61070091905b808211156106fc5760008160009055506001016106e4565b5090565b905600a165627a7a72305820fe4eb67f2da60b0840f2c94565ca6f05fdf8662bc2965f6cba91e95ca6ed2fa20029"

        val FUNC_GETITEMDETAILS = "getItemDetails"

        val FUNC_SETITEMDETAILS = "setItemDetails"

        fun deploy(web3j: Web3j, credentials: Credentials, gasPrice: BigInteger, gasLimit: BigInteger, cId: BigInteger, iCode: BigInteger): RemoteCall<GenuinetySC> {
            val encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.asList<Type<*>>(org.web3j.abi.datatypes.generated.Uint256(cId),
                    org.web3j.abi.datatypes.generated.Uint256(iCode)))
            return Contract.deployRemoteCall(GenuinetySC::class.java, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor)
        }

        fun deploy(web3j: Web3j, transactionManager: TransactionManager, gasPrice: BigInteger, gasLimit: BigInteger, cId: BigInteger, iCode: BigInteger): RemoteCall<GenuinetySC> {
            val encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.asList<Type<*>>(org.web3j.abi.datatypes.generated.Uint256(cId),
                    org.web3j.abi.datatypes.generated.Uint256(iCode)))
            return Contract.deployRemoteCall(GenuinetySC::class.java, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor)
        }

        fun load(contractAddress: String, web3j: Web3j, credentials: Credentials, gasPrice: BigInteger, gasLimit: BigInteger): GenuinetySC {
            return GenuinetySC(contractAddress, web3j, credentials, gasPrice, gasLimit)
        }

        fun load(contractAddress: String, web3j: Web3j, transactionManager: TransactionManager, gasPrice: BigInteger, gasLimit: BigInteger): GenuinetySC {
            return GenuinetySC(contractAddress, web3j, transactionManager, gasPrice, gasLimit)
        }
    }
}