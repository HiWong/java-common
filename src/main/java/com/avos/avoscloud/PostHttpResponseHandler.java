package com.avos.avoscloud;

import com.avos.avoscloud.internal.InternalConfigurationController;
import com.avos.avoscloud.okhttp.internal.framed.Header;

public class PostHttpResponseHandler extends AsyncHttpResponseHandler {

  PostHttpResponseHandler(GenericObjectCallback cb) {
    super(cb);
  }

  // put common json parsing here.
  @Override
  public void onSuccess(int statusCode, Header[] headers, byte[] body) {

    String content = AVUtils.stringFromBytes(body);
    if (InternalConfigurationController.globalInstance().getInternalLogger().isDebugEnabled()) {
      LogUtil.avlog.d(content);
    }
    String contentType = AVUtils.extractContentType(headers);
    if (AVUtils.checkResponseType(statusCode, content, contentType, getCallback()))
      return;

    int code = AVErrorUtils.errorCode(content);
    if (code > 0) {
      if (getCallback() != null) {
        getCallback().onFailure(AVErrorUtils.createException(content), content);
      }
      return;
    }
    if (getCallback() != null) {
      getCallback().onSuccess(content, null);
    }
  }

  @Override
  public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
    String content = AVUtils.stringFromBytes(responseBody);
    if (InternalConfigurationController.globalInstance().getInternalLogger().isDebugEnabled()) {
      LogUtil.avlog.e(content + "\nerror:" + error);
    }
    String contentType = AVUtils.extractContentType(headers);
    if (AVUtils.checkResponseType(statusCode, content, contentType, getCallback()))
      return;

    if (getCallback() != null) {
      getCallback().onFailure(statusCode, error, content);
    }
  }
}
