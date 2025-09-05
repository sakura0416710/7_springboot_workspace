import React, { useEffect } from "react";

const MembersManagement = ({ members }) => {
    const [members, setMembers] = useState([]);

    useEffect(()=>{
        fetch('/react/admin/members') //1.모든 memebr조회, 현재 로그인 된 본인 정보는 빼고 2. 화면출력
        .then(res=>res.json())
        .then(data => setMembers(data))
        .catch(err => console.log(err))
    }, []);

    const [duplicate, setDuplicate] = useState(false);




    //밑에 tbody에서 넘기는 순서대로 받아오고 더블클릭할 경우, 수정하는 것이므로 여기서 수정폼으로 바꾸기
    const rendercell = (value, field, member) =>{ 
        const isEditing = editingCell?.memberId == member.id && editingCell?.field == field; //editingCell라는 변수에 값이 있으려면 handleDoubleClick()이 실행돼서(온 더블클릭 이벤트 실행 시 실행하는 함수) 있어? 있으면 멤버아이디 값 가져오기
        //더블클릭해서 변수에 값이 존재할 경우, 뷰 수정폼으로 변경하기
        if(isEditing){
            if(field == 'email'){
                //작성값이 없을 때 디폴트 값 정해주고
                const safeEditValue = (editValue == '-' || !editValue)? '@naver.com' : editValue;
                const emailSplit = safeEditValue.split('@');
                const emailId = emailSplit[0];
                const emailDomain = emailSplit[1] || 'naver.com';

                return(
                    <>
                        <input 
                            type="text"
                            value={emailId}
                            onChange={e=>setEditValue(e.target.value + '@' + emailDomain)}
                            onKeyUp={handleKeyPress}
                            autoFocus
                            size="5"
                        />
                        @
                        <select value={emailDomain} onChange={e=>setEditValue(emailId + '@' + e.target.value)}>
                            <option value="naver.com">naver.com</option>
                            <option value="hanmail.com">hanmail.com</option>
                            <option value="gmail.com">gmail.com</option>
                            <option value="nate.com">nate.com</option>
                        </select>
                    </>
                )
            } else if(field == 'gender'){
                return(

                    <>
                        <label>
                            <input
                                type="radio"
                                name={`gender-${member.id}`}
                                value="M"
                                checked={editValue == 'M'}
                                onChange={e => setEditValue(e.target.value)}
                                onKeyUp={handleKeyPress}
                            /> M
                        </label>&nbsp;
                        <label>
                            <input
                                type="radio"
                               name={`gender-${member.id}`}
                                value="F"
                                checked={editValue == 'F'}
                                onChange={e => setEditValue(e.target.value)}
                                onKeyUp={handleKeyPress}
                            /> F
                        </label>
                    </>
                );
            
            } else if(field == 'age'){
                return(
                    <input
                        type="number"
                        value={editValue}
                        onChange={handleKeyPress}
                        autoFocus
                        min="0"
                        max="100"
                        size="5"
                    />
                )
            } else if(field == 'isAdmin'){
                return(
                    <>
                        <label>
                            <input
                                type="radio"
                                name={`admin-${member.id}`}
                                value="1"
                                checked={editValue =='1'}
                                onChange = {e => setEditValue(e.target.value)}
                                onKeyUp={handleKeyPress}
                                /> 상위

                        </label>&nbsp;
                        <label>
                            <input
                                type="radio"
                                name={`admin-${member.id}`}
                                value=""
                                checked={editValue =='2'}
                                onChange = {e => setEditValue(e.target.value)}
                                onKeyUp={handleKeyPress}
                                /> 하위

                        </label>&nbsp;
                        <label>
                            <input
                                type="radio"
                                name={`admin-${member.id}`}
                                value="N"
                                checked={editValue =='N'}
                                onChange = {e => setEditValue(e.target.value)}
                                onKeyUp={handleKeyPress}
                                /> X

                        </label>
                    </>
                )
            }


            return(
                <input type="text" v
                    value ={editValue ? editValue : ''} 
                    onChange={e => setEditValue(e.target.value)}
                    autoFocus
                    size="12"
                    onKeyUp={handleKeyPress} //enter눌럿을 때에도 수정폼 저장하기
                    className={duplicate ? 'vibration' : ''}
                />//값에 변화가 생기면 editValue값 변경시키기
            );
        }
                                            //*여기선 순서상관없이 인자를 넘겨줄 수 있지만
        return <span onDoubleClick = {() => handleDoubleClick(member, field, value)}>  
            {field == 'isAdmin' ? (value == '1'? '상위' : (value == '2'? '하위' : '-')) : value || '-'}
            
        </span> 
    }

    const [editingCell, setEditingCell] = useState(); //"누구의 정보"인지와 "어떤 필드"인지 저장하기
    const [editValue, setEditValue] = useState();   //수정할 데이터 저장
    const editableCols = ['name','nickName','email','gender','age','phone','address','isAdmin'];
    const handleDoubleClick = (member, field, value) => { //*또 여기서 받아올 때 순서대로 받아와야 함
        if(!editableCols.includes(field)){  //수정불가능한 항목일 경우 알럿창 띄우기
            alert('수정할 수 없는 항목입니다.');
            return;
        }
        setEditingCell({memberId:member.id, field});
        setEditValue(value);


    }
    //아무 곳 누르면 수정폼 사라지게 하기
    
    useEffect(()=>{
        document.body.addEventListener('click', handleBodyClick);
        
        //cleanup 과정
        return () =>{
            document.body.removeEventListener('click', handleBodyClick);
        }
    }, [editingCell]) //수정폼으로 바뀔 때(더블클릭할때)마다 1회성인 useEffect를 실행시켜라

    const handleBodyClick = e =>{
        if(e.target,tagName != 'INPUT' && editingCell)
            cancleEdit();
    }
    const cancleEdit = () =>{
        setEditValue('');
        setEditingCell(null);
    }

    const handleKeyPress = e => {
        //엔터 눌럿을 때 수정폼저장 하는 함수
        if(e.key == 'Enter'){
            saveEdit();
        }
    }
    const saveEdit = () =>{
        if(!editingCell) return; //값이 안 바꼈으면 그대로 두고
        fetch('/react/admin/members', {
            method : 'put',  //수정하는 REST API
            headers : {'content-type' : 'application/json; charset=UTF-8'},
            body :  JSON.stringify({
                val:editValue,
                col : editingCell.field,
                id : editingCell.memberId
            })   //body에 데이터 보내기 => 객체형식의 JSON으로 보내기
        })
        .then(response => response.json())
        .then(data=> {  //닉네임 중복 시 return -1(sts에서)이였으므로 이 때 vibration을 넣겠다.
            if(data == -1){
                setDuplicate(true);
                setTimeout(()=>setDuplicate(false), 400);
            } else if(data == 1){
                setMembers(prev => prev.map(member => 
                    member.id == editingCell.memberId
                    ?{...member, [editingCell.field] : editValue}
                    : member
                )); //원래 있던 멤버 배열을 map해서 꺼내오면 객체 하나가 나오겠지?
                cancleEdit();
            } else {
                alert('수정을 실패했습니다.');
                cancleEdit();
            }
        })
        .catch(err => conosole.log(err));
    }

   const handleStatusToggle = (member, field, value) =>{
    if(!editValue) return;
    fetch('/react/admin/members',{
        method : 'put',
        headers : {'content-type' : 'application/json; charset=UTF-8'},
        body : JSON.stringify({
            val : value,
            col : field,
            id : member.id
        })
    })
    .then(response => response.json())
    .then(data=> {
        if(data == 1){
            setMembers(prev => prev.map(m => 
                m.id == member.id
                ?{...m, [field]:value}
                : m
            ))
        } else {
            alert('상태변경을 실패하여 페이지가 새로고침 됩니다.');
            location.reload();
        }
    })
    .catch(err => console.log(err))
    
}




  return (
    <>
      <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
					<h1 class="h2">Members</h1>
					<div>
						<span class="info">* 더블클릭을 하면 수정 칸으로 바뀝니다</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<span class="info">* 수정 후 엔터를 누르면 수정이 완료됩니다</span>
					</div>
				</div>
				
				<div class="bd-example">
					<table class="table table-hover" style="text-align: center;">
						<thead>
							<tr>
								<th>ID</th>
								<th>NAME</th>
								<th>NICKNAME</th>
								<th>EMAIL</th>
								<th>GENDER</th>
								<th>AGE</th>
								<th>PHONE</th>
								<th>ADDRESS</th>
								<th>ENROLL</th>
								<th>STATUS</th>
								<th>ADMIN</th>
							</tr>
						</thead>
						<tbody> 
							{members.map((item) => (
                            <tr key={item.id}>
                                <td>{rendercell(item.id, 'id', item)}</td>
                                <td>{rendercell(item.name, 'name', item)}</td>
                                <td>{rendercell(item.nickname, 'nickName', item)}</td>
                                <td>{rendercell(item.email, 'email', item)}</td>
                                <td>{rendercell(item.gender, 'gender', item)}</td>
                                <td>{rendercell(item.age, 'age', item)}</td>
                                <td>{rendercell(item.phone, 'phone', item)}</td>
                                <td>{rendercell(item.address, 'address', item)}</td>
                                <td>{rendercell(item.enroll, 'enrollDate', item)}</td>
                                <td>
                                    <div //같은 상태값일 때는 굳이 함수를 실행하지 않고 반대값으로 바꿀 때에만 함수 실행하도록 만들기.
                                        className={item.memberStatus === "Y" ? "select" : ''}
                                        onClick={() => item.memberStatus == 'N' ? handleStatusToggle(item, 'memberStatus', 'Y') : null}
                                        style={{cursor : 'pointer'}}    
                                    >Y</div>
                                    <div className={item.memberStatus === "N" ? "select" : ''}
                                        onClick={() => item.memberStatus == 'Y' ? handleStatusToggle(item, 'memberStatus', 'N') : null}
                                        style={{cursor : 'pointer'}}
                                    >N</div>
                                </td>
                                <td>{rendercell(item.isAdmin, 'isAdmin', item)}</td>
                            </tr>
                            ))}
                        </tbody>
					</table>
				</div>
            </>
        )}
        

export default MembersManagement;
