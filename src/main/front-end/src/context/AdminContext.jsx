//context API사용하기 : 리액트 내부의 상태를 관리하는 기능
//React 앱 내부에서 상태 관리 차원으로 데이터 정보 공유 시 사용가능
// 리액트 컴포넌트끼리의 상태를 공유하기 위한 도구.

import { createContext, useEffect, useRef, useState } from "react";

/* 사용법 

1.context 생성 :createContext()             */
export const AdminContext = createContext(null); //  .Provider컴포넌트 자동생성:context데이터를 하위 컴포넌트한테 제공
export const AdminProvider = ({children}) => {
    const calledRef = useRef(false); //react에서 값을 저장할 수 있는 곳. 값이 바껴도 컴포넌트를 리렌더링 하지 않음
    //변수 calledRef에 담았지만 실제로는 .current 속성에 값이 저장됨

    const [loading, setLoading] = useState(null); //admin/home넘어가기 전 접근권한 없다 알럿창 뒤로 보이는 화면 안보이게하기
    const [admin, setAdmin] = useState();

    useEffect(() => {
        if (calledRef.current) return;
        calledRef.current = true;

        fetch("/react/admin/users", {
            method: 'get',
            credentials: 'include', //어떤 인증 정보를 포함할 것인가 (sang-origin:같은 오리진 include:다른 포트번호(cross)까지 쿠키나 헤더를 집어넣음))
            headers: { fetch: true }   //{리액트로 보낼 때 구분할 수 있는 key이름: value} ex.{hi:1111}도 됨
        })  //session이 여러 개 일때 세션 구분하기 = JSESSIONID
            .then(res => {                                                      
                if (res.status == 403) {
                    //admin이 아니란 뜻이므로
                    alert('접근 권한이 없습니다.');
                    location.href = 'http://localhost:8080/home';
                    return null;
                } else {
                    setLoading(true); //정상일 땐 화면이 보여야 하므로 true
                }
                return res.json();
            })
            .then(data => setAdmin(data))
            .catch(err => console.log(err))

    }, [])
    if (!loading) return null; //if속 lodaing의 값은 false 

    const value = {admin};
    return(
        <AdminContext.Provider value={value}>
            {children}
        </AdminContext.Provider>
    )
}