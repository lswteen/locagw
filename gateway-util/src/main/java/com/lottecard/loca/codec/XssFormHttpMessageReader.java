package com.lottecard.loca.codec;

import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.FormHttpMessageReader;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * XSS 공격 대응을 위해
 * FormHttpMessageReader form body 변환 중에
 * &lt;, &gt;, &quot;, #, &amp;, &apos;, (, ) 문자를 치환합니다.
 */
public class XssFormHttpMessageReader extends FormHttpMessageReader {
    private final int MAX_BYTE_COUNT;

    public XssFormHttpMessageReader(int maxByteCount) {
        this.MAX_BYTE_COUNT = maxByteCount;
    }

    @Override
    public Mono<MultiValueMap<String, String>> readMono(ResolvableType elementType,
                                                        ReactiveHttpInputMessage message, Map<String, Object> hints) {
        MediaType contentType = message.getHeaders().getContentType();
        Charset charset = getMediaTypeCharset(contentType);

        return DataBufferUtils.join(message.getBody(), MAX_BYTE_COUNT)
                .map(buffer -> {
                    CharBuffer charBuffer = charset.decode(buffer.asByteBuffer());
                    String body = charBuffer.toString()
                            .replace("%3C", "#60") // <
                            .replace("%3E", "#62") // >
                            .replace("%22", "#34") // "
                            .replace("%23", "#35") // #
                            .replace("%26", "#38") // &
                            .replace("%27", "#39") // '
                            .replace("%28", "#40") // (
                            .replace("%29", "#41") // )
                            ;
                    DataBufferUtils.release(buffer);
                    MultiValueMap<String, String> formData = parseFormData(charset, body);
                    return formData;
                });
    }

    private Charset getMediaTypeCharset(@Nullable MediaType mediaType) {
        if (mediaType != null && mediaType.getCharset() != null) {
            return mediaType.getCharset();
        } else {
            return getDefaultCharset();
        }
    }

    private MultiValueMap<String, String> parseFormData(Charset charset, String body) {
        String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>(pairs.length);
        try {
            for (String pair : pairs) {
                int idx = pair.indexOf('=');
                if (idx == -1) {
                    result.add(URLDecoder.decode(pair, charset.name()), null);
                } else {
                    String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
                    String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
                    result.add(name, value);
                }
            }
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
        return result;
    }
}
