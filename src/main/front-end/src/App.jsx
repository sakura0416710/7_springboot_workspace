import { useEffect, useState } from "react"
import AdminMain from './layouts/AdminMain';


function App() {
  // const [data, setData] = useState('');
  
  // //window.onload와 비슷함. use로 시작했기 때문에 훅. 컴포넌트가 렌더링 된 이후 실행되는 코드를 작성할 때 사용
  // //이미지,js,css가 모두 다운이 된 다음 한 번 실행되는 윈도우.온로드와 다르게
  // //useEffect는 컴포넌트 수 만큼 개별적으로 자동하고 리액트의 특징 상 마운트, 즉 읽은 다음 렌더링이 되는데 그게 거의 동시에 되는 것.
  // //컴포넌트가 DOM에 붙으면 즉시 실행돼서 살짝 더 빠르다.


  // //인자 2개.
  //   //{실행할 코드}, [의존성]);
  //   //의존성은 선택. useEffect의 실행시점을 의존하겠다해서 의존성 배열임

  //   /* 패턴1 : 의존성 배열이 없을 때. 원하지 않을 때 실행이 될 수 있음(성능문제 발생가능)
  //     모든 렌더링 후에 매번 실행 -> 성능 문제 유발로 거의 안 씀
  //     ex.
  //     useEffect(()=>{
  //         console.log('컴포넌트가 매번 렌더링 될때마다 실행');
  //       });

  //     패턴2 : 의존성 배열이 비어있을 때
  //     컴포넌트가 처음에 나타날 때만 한 번만 실행함 -> api요청, 초기설정 등에 사용
  //     ex.
  //     useEffect (()=>{
  //       console.log('처음 나타날 때 한 번 실행');
        
  //       }, []);


  //     패턴3: 의존성 배열에 값이 있을 때
  //     의존성에 들어간 값이 바뀔 때마다 실행->state, props 감시할때 사용
  //     ex1.
  //       useEffect(()=>{
  //         console.log('data가 ${data}값이다');
  //         }, [data]);

  //     ex2. 무한반복
  //       useEffect(()=>{
  //         setData(data+1);
  //         },[data]);
  
  //       */

  // useEffect(()=>{
  //   fetch('react/data')
  //   .then(res=>res.json())
  //   .then(res => setData(res.data))
  //   .catch(err => console.log(err))
  // }, []);
  // return(
  //   <div>
  //     받아온 값 : {data}

  //   </div>
  // );

  return(
    <AdminMain/>
  );
  
}

export default App
