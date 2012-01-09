// Automatically Generated -- DO NOT EDIT
// com.mediacontrol.client.MyRequestFactory
package com.mediacontrol.client;
import java.util.Arrays;
import com.google.web.bindery.requestfactory.vm.impl.OperationData;
import com.google.web.bindery.requestfactory.vm.impl.OperationKey;
public final class MyRequestFactoryDeobfuscatorBuilder extends com.google.web.bindery.requestfactory.vm.impl.Deobfuscator.Builder {
{
withOperation(new OperationKey("_wDaBga11NlIsU6yn1ZGPoeH2KA="),
  new OperationData.Builder()
  .withClientMethodDescriptor("()Lcom/google/web/bindery/requestfactory/shared/InstanceRequest;")
  .withDomainMethodDescriptor("()Ljava/lang/String;")
  .withMethodName("send")
  .withRequestContext("com.mediacontrol.client.MyRequestFactory$MessageRequest")
  .build());
withOperation(new OperationKey("tpegE7JfvA2Y1Ul157LjNW1ojcM="),
  new OperationData.Builder()
  .withClientMethodDescriptor("()Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("()Ljava/lang/String;")
  .withMethodName("getMessage")
  .withRequestContext("com.mediacontrol.client.MyRequestFactory$HelloWorldRequest")
  .build());
withOperation(new OperationKey("KlGC4mUZNNPmbeF8HbFdUFA1AAo="),
  new OperationData.Builder()
  .withClientMethodDescriptor("()Lcom/google/web/bindery/requestfactory/shared/InstanceRequest;")
  .withDomainMethodDescriptor("()V")
  .withMethodName("register")
  .withRequestContext("com.mediacontrol.client.MyRequestFactory$RegistrationInfoRequest")
  .build());
withOperation(new OperationKey("4H_NUv2Iqap$seVP3DqIuKO5LFg="),
  new OperationData.Builder()
  .withClientMethodDescriptor("()Lcom/google/web/bindery/requestfactory/shared/InstanceRequest;")
  .withDomainMethodDescriptor("()V")
  .withMethodName("unregister")
  .withRequestContext("com.mediacontrol.client.MyRequestFactory$RegistrationInfoRequest")
  .build());
withRawTypeToken("8KVVbwaaAtl6KgQNlOTsLCp9TIU=", "com.google.web.bindery.requestfactory.shared.ValueProxy");
withRawTypeToken("FXHD5YU0TiUl3uBaepdkYaowx9k=", "com.google.web.bindery.requestfactory.shared.BaseProxy");
withRawTypeToken("BkTwHiHtCfPSil0U6zsHRtlEDso=", "com.mediacontrol.shared.MessageProxy");
withRawTypeToken("05XjDMMXr3zgO$nTLEzfeLzAgX8=", "com.mediacontrol.shared.RegistrationInfoProxy");
withClientToDomainMappings("com.mediacontrol.server.Message", Arrays.asList("com.mediacontrol.shared.MessageProxy"));
withClientToDomainMappings("com.mediacontrol.server.RegistrationInfo", Arrays.asList("com.mediacontrol.shared.RegistrationInfoProxy"));
}}
