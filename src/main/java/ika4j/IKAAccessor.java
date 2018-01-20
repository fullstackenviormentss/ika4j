/*
 * Copyright 2018 Yusuke Yamamoto
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ika4j;

import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

class IKAAccessor {

    static String getSchedule(String iksmSession) {
        CookieManager cookieManager = new CookieManager();
        HttpCookie cookie = new HttpCookie("iksm_session", iksmSession);
        cookie.setDomain("app.splatoon2.nintendo.net");
        cookie.setPath("/");
        cookie.setMaxAge(86399);
        cookie.setVersion(0);
        try {
            cookieManager.getCookieStore()
                    .add(new URI("https://app.splatoon2.nintendo.net/api/schedules"), cookie);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }

        ForceHostnameVerificationSSLContext ctx = new ForceHostnameVerificationSSLContext("app.splatoon2.nintendo.net", 443);

        final HttpClient client = HttpClient
                .newBuilder()
                .sslContext(ctx).sslParameters(ctx.getParametersForSNI())
                .cookieManager(cookieManager)
                .build();
        final HttpRequest request = HttpRequest.newBuilder(URI.create("https://app.splatoon2.nintendo.net/api/schedules"))
                .GET()
                .build();
        try {
            HttpResponse<String> httpResponse = client.send(request,
                    HttpResponse.BodyHandler.asString(StandardCharsets.UTF_8));
            return httpResponse.body();
//            httpResponse
//                    .thenApply(HttpResponse::body)
//                    .thenApply(s -> String.format("[%s] - %s", Thread.currentThread().getName(), s))
//                    .thenAccept(System.out::println)
//                    .join();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

//        client.sendAsync(request,
//                HttpResponse.BodyHandler.asString(StandardCharsets.UTF_8))
//                .thenApply(HttpResponse::body)
//                .thenApply(s -> String.format("[%s] - %s", Thread.currentThread().getName(), s))
//                .thenAccept(System.out::println)
//                .join();
    }
}