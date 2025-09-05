import { Chart } from "chart.js/auto"; //기본 이외에 필요한 import자동화
import { useEffect, useRef } from "react";


const ChartSection = ({title, type}) => {
    const canvasRef = useRef();      //리액트에서 값을 저장, (.current에) 값 바껴도 리렌더링 되지X
    const chartRef = useRef();

    useEffect(() => { //인자2개( 실행대상 함수, 의존성배열)
        fetch('/react/admin/logs')
        .then(res=>res.json())
        .then(data=> {
            if(type == 'seven') sevenChart(data);
            else allChart(data);
        })
        .catch(err => console.log(err))
        //컴포넌트가 언마운트 됐을 때는 차트 제거 (DOM에 컴포넌트가 붙지 않았을 때)
        return()=>{
            if(chartRef.current){
                chartRef.current.destroy();
            }
        }


    }, [type])      //타입이 바뀔 때마다 차트가 새로 나오는 것이므로 의존성 기준을 type으로 잡기

    const getDateFormat = date =>{
         //날짜 데이터 가져와서 형태 바꾸기
        const year = (date.getFullYear() + '').slice(-2);1
        const month = ('0' + (date.getMonth()+1)).slice(-2);
        const day = ('0' + date.getDate()).slice(-2);

        return year + '-' + month + '-' + day;
    }

    const getThatDays = (today, n) => {
        return new Date(new Date().setDate(today.getDate() - n));
    }
    const sevenChart = data => {
        const ctx = canvasRef.current;
        const today = new Date();
        if(chartRef.current){
            chartRef.current.destroy();
        }

        const dates = [];
        const counts = [];
        for (let i= 6; i>= 0; i--){
           const key = getDateFormat(getThatDays(today, i));
           dates.push(key);
           counts.push(data[key] || 0);
        }


        chartRef.current = new Chart(ctx, {
            type:'line',
            data:{
                labels:dates,
                datasets:[{
                    data : counts,
                    lineTension: 0,
                    backgroundColor: 'transparent',
                    borderColor: '#007bff',
                    borderWidth: 4,
                    pointBackgroundColor: '#007bff'
                }]

                },
                options: {
                    plugins: {
                        legend: {
                            display: false
                        },
                        tooltip: {
                            boxPadding: 3
                        }
                    }
                }
            });
        }


        const allChart = data =>{
            const ctx = canvasRef.current;

            if(chartRef.current){
                chartRef.current.destroy();
            }

            const dates = [];
            const counts = [];
            for(const key in data){
                dates.push(key);
                counts.push(data[key]);
            }

            chartRef.current = new Chart(ctx, {
            type:'line',
            data:{
                labels: dates,
                datasets:[{
                    data:counts,
                    lineTension: 0,
                    backgroundColor: 'transparent',
                    borderColor: '#007bff',
                    borderWidth: 4,
                    pointBackgroundColor: '#007bff'

                    }]
                }
                
            });
        }

    return(
        <div>
            
            <h4>
                {title}
            </h4>
            <canvas className="my-4 w-100" ref={canvasRef} width="900" height="250"></canvas>

            

        </div>
    );
}
export default ChartSection;