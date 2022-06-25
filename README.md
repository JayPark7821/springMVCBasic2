* ### 스프링MVC - 기본기능
* @RestController
  * @Controller 는 반환 값이 String 이면 뷰 이름으로 인식된다. 그래서 뷰를 찾고 뷰가 랜더링 된다.
  * @RestController 는 반환 값으로 뷰를 찾는 것이 아니라, HTTP 메시지 바디에 바로 입력한다.
  따라서 실행 결과로 ok 메세지를 받을 수 있다. @ResponseBody 와 관련이 있는데, 뒤에서 더 자세히
  설명한다

* ### 올바른 로그 사용법
> log.debug("data="+data)
>
> * 로그 출력 레벨을 info로 설정해도 해당 코드에 있는 "data="+data가 실제 실행이 되어 버린다.
결과적으로 문자 더하기 연산이 발생한다.

> log.debug("data={}", data)
>
> * 로그 출력 레벨을 info로 설정하면 아무일도 발생하지 않는다. 따라서 앞과 같은 의미없는 연산이
발생하지 않는다

* ### 둘다 허용
  * 매핑 : /hello-basic
  * url 요청 : /hello-basic , /hello-basic/ 
  * 둘다 스프링이 알아서 매핑해준다.

> RequestMapping 에 method 속성으로 HTTP 메서드를 지정하지 않으면 HTTP 메서드와 무관하게
호출된다.
>
> 모두 허용 GET, HEAD, POST, PUT, PATCH, DELETE
>


* ### @RequestParam.required
  * 파라미터 필수 여부
  * 기본값이 파라미터 필수( true )이다.
* ### /request-param 요청
  * username 이 없으므로 400 예외가 발생한다.
  
* ### 주의! - 파라미터 이름만 사용
  * /request-param?username=
  * 파라미터 이름만 있고 값이 없는 경우 빈문자로 통과
  
* ### 주의! - 기본형(primitive)에 null 입력
  * /request-param 요청
  * @RequestParam(required = false) int age
  * null 을 int 에 입력하는 것은 불가능(500 예외 발생)
  * 따라서 null 을 받을 수 있는 Integer 로 변경하거나, 또는 다음에 나오는 defaultValue 사용

* @RequestParam Map ,
  * Map(key=value)
* @RequestParam MultiValueMap
  * MultiValueMap(key=[value1, value2, ...] ex) (key=userIds, value=[id1, id2])


  * 파라미터의 값이 1개가 확실하다면 Map 을 사용해도 되지만, 그렇지 않다면 MultiValueMap 을 사용하자. 

> HttpEntity , @RequestBody를 사용하면 HTTP메시지 컨버터가  HTTP메시지 바디의 내용을 우리가 원하는 문자나 객체등으로 변환해줌.


```java
/**
* @RequestBody 생략 불가능(@ModelAttribute 가 적용되어 버림)
* HttpMessageConverter 사용 -> MappingJackson2HttpMessageConverter (contenttype: application/json)
*
*/
@ResponseBody
@PostMapping("/request-body-json-v3")
public String requestBodyJsonV3(@RequestBody HelloData data) {
  log.info("username={}, age={}", data.getUsername(), data.getAge());
  return "ok";
}
```


* ### 스프링은 @ModelAttribute , @RequestParam 과 같은 해당 애노테이션을 생략시 다음과 같은 규칙을 적용한다.
  * String , int , Integer 같은 단순 타입 = @RequestParam
  * 나머지 = @ModelAttribute (argument resolver 로 지정해둔 타입 외

> * 주의
> 
> HTTP 요청시에 content-type이 application/json인지 꼭! 확인해야 한다. 그래야 JSON을 처리할 수
있는 HTTP 메시지 컨버터가 실행된다
>


* ### String을 반환하는 경우 - View or HTTP 메시지
  * `@ResponseBody` 가 없으면 `response/hello` 로 뷰 리졸버가 실행되어서 뷰를 찾고, 렌더링 한다.
  * `@ResponseBody` 가 있으면 뷰 리졸버를 실행하지 않고, HTTP 메시지 바디에 직접 `response/hello` 라는
  문자가 입력된다.
  * 여기서는 뷰의 논리 이름인 response/hello 를 반환하면 다음 경로의 뷰 템플릿이 렌더링 되는 것을 확인할 수 있다.
    * 실행: templates/response/hello.html
  
* ### Void를 반환하는 경우
  * `@Controller` 를 사용하고, `HttpServletResponse` , `OutputStream(Writer)` 같은 HTTP 메시지 바디를 처리하는 파라미터가 없으면 요청 URL을 참고해서 논리 뷰 이름으로 사용
  * 요청 URL: /response/hello
  * 실행: templates/response/hello.html
  * > 참고로 이 방식은 명시성이 너무 떨어지고 이렇게 딱 맞는 경우도 많이 없어서, 권장하지 않는다.

* ### HTTP 메시지
  * @ResponseBody , HttpEntity 를 사용하면, 뷰 템플릿을 사용하는 것이 아니라, HTTP 메시지 바디에
  직접 응답 데이터를 출력할 수 있다


```java
// @RestController -> @Controller , @ResponseBody 모두 들고있다
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
@ResponseBody
public @interface RestController {

	/**
	 * The value may indicate a suggestion for a logical component name,
	 * to be turned into a Spring bean in case of an autodetected component.
	 * @return the suggested component name, if any (or empty String otherwise)
	 * @since 4.0.1
	 */
	@AliasFor(annotation = Controller.class)
	String value() default "";

}

```

* ### HTTP 메시지 컨버터
![image](https://user-images.githubusercontent.com/60100532/175766142-c1ae78da-20c3-4d6b-8a4a-eebc42ab240d.png)

* ### 스프링 MVC는 다음의 경우에 HTTP 메시지 컨버터를 적용한다.
> * HTTP 요청 : `@RequestBody`, `HttpEntity(RequestEntity)` 
> * HTTP 응당 : `@ResponseBody`, `HttpEntity(ResponseEntity)`
>



* ### HTTP 메시지 컨버터 인터페이스
`org.springframework.http.converter.HttpMessageConverter`

```java
       
package org.springframework.http.converter;
public interface HttpMessageConverter<T> {
  boolean canRead(Class<?> clazz, @Nullable MediaType mediaType);

  boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType);

  List<MediaType> getSupportedMediaTypes();

  T read(Class<? extends T> clazz, HttpInputMessage inputMessage)
          throws IOException, HttpMessageNotReadableException;

  void write(T t, @Nullable MediaType contentType, HttpOutputMessage
          outputMessage)
          throws IOException, HttpMessageNotWritableException;

}
```

* ### HTTP 메시지 컨버터는 HTTP 요청 , 응답 둘 다 사용된다.
* `canRead()`, `canWrite()` : 메시지 컨버터가 해당 클래스, 미디어타입을 지원하는지 체크
* `read()`, `write()` :  메시지 컨버터를 통해서 메시지를 읽고 쓰는 기능

> 스프링 부트는 다양한 메시지 컨버터를 제공하는데, 대상 클래스 타입과 미디어 타입 ( content-type) 둘을 체크해서
사용여부를 결정한다. 만약 만족하지 않으면 다음 메시지 컨버터로 우선순위가 넘어간다
>


* ### HTTP 요청 데이터 읽기
  - HTTP 요청이 오고, 컨트롤러에서 `@RequestBody` , `HttpEntity` 파라미터를 사용한다.
  - 메시지 컨버터가 메시지를 읽을 수 있는지 확인하기 위해 `canRead()` 를 호출한다.
    - 대상 클래스 타입을 지원하는가.
      - 예) @RequestBody 의 대상 클래스 ( `byte[]` , `String` , `HelloData` )
    - HTTP 요청의 **_Content-Type_** 미디어 타입을 지원하는가.
      - 예) text/plain , application/json , */*
  - `canRead()` 조건을 만족하면 `read()` 를 호출해서 객체 생성하고, 반환한다

* ### HTTP 응답 데이터 생성
  - 컨트롤러에서 `@ResponseBody` , `HttpEntity` 로 값이 반환된다.
  - 메시지 컨버터가 메시지를 쓸 수 있는지 확인하기 위해 `canWrite()` 를 호출한다.
    - 대상 클래스 타입을 지원하는가.
      - 예) return의 대상 클래스 ( byte[] , String , HelloData )
    - HTTP 요청의 _**Accept**_ 미디어 타입을 지원하는가.(더 정확히는 @RequestMapping 의 produces )
      - 예) text/plain , application/json , */*
  - `canWrite()` 조건을 만족하면 `write()` 를 호출해서 HTTP 응답 메시지 바디에 데이터를 생성한다


```java

    /**
     * Content-Type 헤더 기반 추가 매핑 Media Type
     * consumes="application/json"
     * consumes="!application/json"
     * consumes="application/*"
     * consumes="*\/*"
     * MediaType.APPLICATION_JSON_VALUE
     */
    @PostMapping(value = "/mapping-consume", consumes = "application/json")
    public String mappingConsumes() {
        log.info("mappingConsumes");
        return "ok";
    }

    /**
     * Accept 헤더 기반 Media Type
     * produces = "text/html"
     * produces = "!text/html"
     * produces = "text/*"
     * produces = "*\/*"
     */
    @PostMapping(value = "/mapping-produce", produces = "text/html")
    public String mappingProduces() {
        log.info("mappingProduces");
        return "ok";
    }
    
```

>* ### StringHttpMessageConverter
> 
>```java
>// content-type: application/json
>@RequestMapping
>void hello(@RequetsBody String data) {
>
>}
>```
>

>* ### MappingJackson2HttpMessageConverter
>
>```java
>// content-type: application/json
>@RequestMapping
>void hello(@RequetsBody HelloData data) {z
>
>}
>```
>


![image](https://user-images.githubusercontent.com/60100532/175772206-fb8effd9-7902-45fe-8263-d185915fd0b7.png)

* ### ArgumentResolver
  - 어노테이션 기반의 컨트롤러는 다양한 파라미터를 사용할 수 있음.
  - `HttpServletRequest`, `Model` 
  - `@RequestParam`, `@ModelAttribute` 같은 어노테이션
  - `@RequestBody``HttpEntity` 같은 HTTP 메시지를 처리하는 부분까지 매우 큰 유연함을 가지고있다.
  - 이렇게 파라미터를 유연하게 처리할 수 있는 이유가 바로 `ArgumentResolver` 덕분이다.


  - 어노테이션 기반 컨트롤러를 처리하는 `RequestMappingHandlerAdaptor`는 바로 이 `ArgumentResolver`를 호출해서 컨트롤러(핸들러)가 필요로 하는 다양한 파라미터의 값(객체)을 생성한다.
  - 그리고 이렇게 파라미터의 값이 모두 준비되면 컨트롤러를 호출하면서 값을 넘겨 준다. 


```java

package org.springframework.web.method.support;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;

 
public interface HandlerMethodArgumentResolver {
    
	boolean supportsParameter(MethodParameter parameter);
    
	@Nullable
	Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception;

}
```

* ### 동작 방식
* `ArgumentResolver`의 `supportsParameter()`를 호출해서 해당 파라미터를 지원하는지 체크하고,
* 지원하면 `resolveArgument()`를 호출해서 실제 객체를 생성한다.
* 생성된 객체가 컨트롤러 호출시 넘어간다.

* ### ReturnValueHandler
* `HandlerMethodReturnValueHandler` 를 줄여서 `ReturnValueHandler` 라 부른다.
* `ArgumentResolver` 와 비슷한데, 이것은 응답 값을 변환하고 처리한다

> Controller에서 String으로 뷰 이름을 반환해도, 동작하는 이유가 바로 ReturnValueHandler덕분이다.


* ### HTTP 메시지 컨버터
![image](https://user-images.githubusercontent.com/60100532/175772803-efa87491-b95b-4407-adc4-6cb9f5774cb8.png)

* 요청의 경우 `@RequestBody` 를 처리하는 `ArgumentResolver` 가 있고, 
* `HttpEntity` 를 처리하는 `ArgumentResolver` 가 있다. 
* 이 `ArgumentResolver` 들이 HTTP 메시지 컨버터를 사용해서 필요한 객체를 생성하는 것이다.

![image](https://user-images.githubusercontent.com/60100532/175772946-18dfe972-6daa-4606-be9f-15f58a8d415c.png)



```java

package org.springframework.web.servlet.mvc.method.annotation;

public class HttpEntityMethodProcessor extends AbstractMessageConverterMethodProcessor {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return (HttpEntity.class == parameter.getParameterType() ||
            RequestEntity.class == parameter.getParameterType());
  }

  @Override
  public boolean supportsReturnType(MethodParameter returnType) {
    return (HttpEntity.class.isAssignableFrom(returnType.getParameterType()) &&
            !RequestEntity.class.isAssignableFrom(returnType.getParameterType()));
  }
}


public class RequestEntity<T> extends HttpEntity<T> {
    
}
```

* ### 응답의 경우 
* `@ResponseBody` 와 `HttpEntity` 를 처리하는 `ReturnValueHandler` 가 있다.
* 여기에서 HTTP 메시지 컨버터를 호출해서 응답 결과를 만든다

* 스프링 MVC는 `@RequestBody` `@ResponseBody` 가 있으면
* `RequestResponseBodyMethodProcessor` (ArgumentResolver)
* `HttpEntity` 가 있으면 `HttpEntityMethodProcessor` (ArgumentResolver)를 사용한다

![image](https://user-images.githubusercontent.com/60100532/175773153-d643b85b-526a-4329-84e7-6af0bc270e53.png)


> 스프링이 필요한 대부분의 기능을 제공하기 때문에 실제 기능을 확장할 일이 많지는 않다. 기능 확장은
> `WebMvcConfigurer` 를 상속 받아서 스프링 빈으로 등록하면 된다. 
> 실제 자주 사용하지는 않으니 실제 기능 확장이 필요할 때 `WebMvcConfigurer` 를 검색해보자


```java
@Bean
public WebMvcConfigurer webMvcConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
          //...
      }
      @Override
      public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
      //...
      }
    };
}
```