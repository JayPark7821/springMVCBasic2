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