하기 내용을 반드시 숙지하고 시험 응시 바랍니다.

+-----------------------------+
| 공통 사항                   |
+-----------------------------+

1. 파일 구성
   - .metadata     : eclipse workspace 설정
   - .recommenders : eclipse workspace 설정
   - README.TXT    : 본 파일
   - SUB1 ~        : 소문항별 디렉토리
   -   .settings   : eclipse 프로젝트 설정
   -   .classpath  : eclipse 프로젝트 설정
   -   .project    : eclipse 프로젝트 설정
   -   bin         : 빌드된 .class 디렉토리
   -   src         : .java (소스코드) 디렉토리
   -   SP_TEST.BAT : 콘솔에서 작성한 java 프로그램을 실행시킬 BAT 파일
                     (채점시 사용되는 파일로 하기 '제출 방법' 필독)
   - 소문항별 샘플데이터는 따로 명시하지 않는 경우 소문항별 폴더(SUB1, SUB2, ...) 바로 아래에 있음.

2. eclipse 설정 방법
   1) 제공파일 압축을 푼다. 디렉토리 구조 및 이름은 변경하지 않는다.
   2) eclipse 실행
   3) File => Switch Workspace => Other 선택
   4) 팝업된 Workspace Launcher 창에서 Browse 버튼을 누르고 압축 해제한 디렉토리 선택
      ex) C:\ssp_workspace\ssp_java_test
   5) eclipse 제기동됨
   6) Project Explorer에서 소문항별 프로젝트 확인
      ex) SUB1 ... SUB5

3. 제출 방법
 - 압축해제한 제공파일 디렉토리에서 문제 풀이 후 다시 "C:\ssp_workspace\ssp_java_test" 전체 압축하여 제출함.
 - 체점시 각 소문항별 디렉토리(SUB1, SUB2, ...)에서 SP_TEST.BAT를 실행함.
 
   * SP_TEST.BAT의 내용은 다음과 같음.
     java -classpath bin com.lgcns.test.RunManager
   
   따라서 제공된 com.lgcns.test.RunManager의 main 메소드에서 프로그램이 시작되도록 구현해야 함.
   별도의 외부 class들이나 jar 파일 사용시 SP_TEST.BAT의 java 커맨드 옵션에 해당 내용을 반드시 반영하여,
   채점시 정상적으로 동작할 수 있도록 해야 함.

 - SUB2 이후의 내용은 이전 소문항 개발 내용을 본인이 직접 복사하여 사용함.
   (단, eclipse 프로젝트 관련 파일들은 복사하지 말것)
   (이전 소문항 src 디렉토리 내용을 현 소문항 src 디렉토리에 복사하면 소스코드 복사됨)
 - 제출시 빌드 가능한 소스파일이 각 소문항별로 반드시 포함되어야 점수 인정함

+-----------------------------+
| 문항별 상세                 |
+-----------------------------+

[소문항3~5] 제공되는 메시지코드 변환 프로그램 안내
	1) 프로그램 설명
	  - CODECONV.EXE
	  - 기본 사용법
	    변환하고자 하는 코드를 Command Line Argument로 하여 실행하면
	    콘솔 표준출력(stdout)으로 변환된 코드가 출력됨
	    예) C:\>CODECONV MESSAGE01<엔터키>
	        M1068042C
	        C:\>
	  - 외부 프로그램 실행 기술
	    작성하는 프로그램에서는 메시지코드 변환시, '변환할 메시지코드'를 Command Line Argument로
	    CODECONV.EXE를 실행해야 하며, CODECONV.EXE가 콘솔 표준출력(stdout)으로 출력하는
	    '변환된 메시지코드 결과'를 가져올 수 있어야 한다.
	    
	    즉, 원하는 Command Line Argument로 CODECONV.EXE를 실행시킬 수 있어야 하며, 이때
	    CODECONV.EXE에서 출력하는 콘솔 표준출력(stdout)을 프로그램에서 가져올 수 있어야 한다.


