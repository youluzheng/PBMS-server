package org.pbms.pbmsserver.controller;

import cn.hutool.json.JSONUtil;
import org.pbms.pbmsserver.common.auth.TokenBean;
import org.pbms.pbmsserver.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基本的调用类，可以在这里写一些我们常用的调用方法
 *
 * @author zqs
 * @author zyl
 */

@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 通过重写此方法判断是否需要认证，null表示不需要，默认为null
     *
     * @return tokenBean
     */
    protected TokenBean getTokenBean() {
        return null;
    }

    private void setToken(MockHttpServletRequestBuilder builder) {
        TokenBean tokenBean = this.getTokenBean();
        if (tokenBean != null) {
            builder.header("token", TokenUtil.generateToken(tokenBean));
        }
    }

    protected ResultActions get(String url, Map<String, String> params, Map<String, Object> headers) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(url);
        this.setToken(builder);
        if (null != params) {
            params.forEach(builder::param);
        }
        if (null != headers) {
            headers.forEach(builder::header);
        }
        return this.mockMvc.perform(builder);
    }

    protected ResultActions get(String url, Map<String, String> params) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(url);
        this.setToken(builder);
        if (null != params) {
            params.forEach(builder::param);
        }
        return this.mockMvc.perform(builder);
    }

    protected ResultActions get(String url, String paramKey1, String paramValue1) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(paramKey1, paramValue1);
        return get(url, params);
    }

    protected ResultActions get(String url, String paramKey1, String paramValue1, String paramKey2, String paramValue2) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(paramKey1, paramValue1);
        params.put(paramKey2, paramValue2);
        return get(url, params);
    }

    protected ResultActions get(String url) throws Exception {
        return get(url, null);
    }

    protected ResultActions delete(String url, Map<String, String> params, Map<String, Object> headers) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(url);
        this.setToken(builder);
        if (null != params) {
            params.forEach(builder::param);
        }
        if (null != headers) {
            headers.forEach(builder::header);
        }
        return this.mockMvc.perform(builder);
    }

    protected ResultActions delete(String url, Map<String, String> params) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(url);
        this.setToken(builder);
        if (null != params) {
            params.forEach(builder::param);
        }
        return this.mockMvc.perform(builder);
    }

    protected ResultActions delete(String url, String paramKey1, String paramValue1) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(paramKey1, paramValue1);
        return delete(url, params);
    }

    protected ResultActions delete(String url, String paramKey1, String paramValue1, String paramKey2, String paramValue2) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(paramKey1, paramValue1);
        params.put(paramKey2, paramValue2);
        return delete(url, params);
    }

    protected ResultActions delete(String url) throws Exception {
        return delete(url, null);
    }

    protected ResultActions post(String url, Map<String, String> params, Map<String, Object> headers, Object data, MediaType mediaType) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(url);
        this.setToken(builder);
        if (null != params) {
            params.forEach(builder::param);
        }
        if (null != headers) {
            headers.forEach(builder::header);
        }
        if (null != data) {
            builder.content(JSONUtil.toJsonStr(data));
            builder.contentType(mediaType);
        }
        return this.mockMvc.perform(builder);
    }

    protected ResultActions post(String url, Map<String, String> params, Object data, MediaType mediaType) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(url);
        this.setToken(builder);
        if (null != params) {
            params.forEach(builder::param);
        }
        if (null != data) {
            builder.content(JSONUtil.toJsonStr(data));
            builder.contentType(mediaType);
        }
        return this.mockMvc.perform(builder);
    }

    protected ResultActions post(String url, Map<String, String> params, Object data) throws Exception {
        return post(url, params, data, MediaType.APPLICATION_JSON);
    }

    protected ResultActions post(String url, Object data, MediaType mediaType) throws Exception {
        return post(url, null, data, mediaType);
    }

    protected ResultActions post(String url, Object data) throws Exception {
        return post(url, null, data, MediaType.APPLICATION_JSON);
    }

    protected ResultActions post(String url) throws Exception {
        return post(url, null, null, MediaType.APPLICATION_JSON);
    }

    protected ResultActions put(String url, Map<String, String> params, Map<String, Object> headers, Object data, MediaType mediaType) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(url);
        this.setToken(builder);
        if (null != params) {
            params.forEach(builder::param);
        }
        if (null != headers) {
            headers.forEach(builder::header);
        }
        if (null != data) {
            builder.content(JSONUtil.toJsonStr(data));
            builder.contentType(mediaType);
        }
        return this.mockMvc.perform(builder);
    }

    protected ResultActions filePost(String url, Map<String, String> params, Map<String, Object> headers, List<MockMultipartFile> files) throws Exception {
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(url);
        this.setToken(builder);
        if (files != null) {
            files.forEach(builder::file);
        }
        if (null != params) {
            params.forEach(builder::param);
        }
        if (null != headers) {
            headers.forEach(builder::header);
        }
        return this.mockMvc.perform(builder);
    }

    protected ResultActions filePost(String url, Map<String, String> params, Map<String, Object> headers, MockMultipartFile file) throws Exception {
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(url).file(file);
        this.setToken(builder);
        if (null != params) {
            params.forEach(builder::param);
        }
        if (null != headers) {
            headers.forEach(builder::header);
        }
        return this.mockMvc.perform(builder);
    }

    protected ResultActions filePost(String url, Map<String, String> params, List<MockMultipartFile> files) throws Exception {
        return this.filePost(url, params, null, files);
    }

    protected ResultActions filePost(String url, Map<String, String> params, MockMultipartFile file) throws Exception {
        return this.filePost(url, params, null, file);
    }

    protected ResultActions filePost(String url, List<MockMultipartFile> files) throws Exception {
        return this.filePost(url, null, null, files);
    }

    protected ResultActions filePost(String url, MockMultipartFile file) throws Exception {
        return this.filePost(url, null, null, file);
    }

    protected ResultActions put(String url, Map<String, String> params, Object data, MediaType mediaType) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(url);
        this.setToken(builder);
        if (null != params) {
            params.forEach(builder::param);
        }
        if (null != data) {
            builder.content(JSONUtil.toJsonStr(data));
            builder.contentType(mediaType);
        }
        return this.mockMvc.perform(builder);
    }

    protected ResultActions put(String url, Map<String, String> params, Object data) throws Exception {
        return put(url, params, data, MediaType.APPLICATION_JSON);
    }

    protected ResultActions put(String url, Object data, MediaType mediaType) throws Exception {
        return put(url, null, data, mediaType);
    }

    protected ResultActions put(String url, Object data) throws Exception {
        return put(url, null, data, MediaType.APPLICATION_JSON);
    }

    protected ResultActions put(String url) throws Exception {
        return put(url, null, null, MediaType.APPLICATION_JSON);
    }

    protected ResultActions patch(String url, Map<String, String> params, Map<String, Object> headers, Object data, MediaType mediaType) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.patch(url);
        this.setToken(builder);
        if (null != params) {
            params.forEach(builder::param);
        }
        if (null != headers) {
            headers.forEach(builder::header);
        }
        if (null != data) {
            builder.content(JSONUtil.toJsonStr(data));
            builder.contentType(mediaType);
        }
        return this.mockMvc.perform(builder);
    }

    protected ResultActions patch(String url, Map<String, String> params, Object data, MediaType mediaType) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.patch(url);
        this.setToken(builder);
        if (null != params) {
            params.forEach(builder::param);
        }
        if (null != data) {
            builder.content(JSONUtil.toJsonStr(data));
            builder.contentType(mediaType);
        }
        return this.mockMvc.perform(builder);
    }

    protected ResultActions patch(String url, Map<String, String> params, Object data) throws Exception {
        return patch(url, params, data, MediaType.APPLICATION_JSON);
    }

    protected ResultActions patch(String url, Object data, MediaType mediaType) throws Exception {
        return patch(url, null, data, mediaType);
    }

    protected ResultActions patch(String url, Map<String, String> params) throws Exception {
        return patch(url, params, null, MediaType.APPLICATION_JSON);
    }

    protected ResultActions patch(String url) throws Exception {
        return patch(url, null, null, MediaType.APPLICATION_JSON);
    }
}