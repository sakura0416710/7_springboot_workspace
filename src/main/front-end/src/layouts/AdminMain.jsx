import { useContext } from 'react';
import AdminSymbol from '../components/AdminSymbol';
import Adminbar from '../components/Adminbar';
import Navbar from '../components/Navbar';
import { AdminContext } from '../context/AdminContext';
import { Outlet } from 'react-router-dom';

const AdminMain = () =>{
    const {admin} = useContext(AdminContext);
    
    return(
        <div>
            <Navbar/>
            <AdminSymbol/>
            

            <div className="container-fluid">
                <div className="row">
                    <Adminbar/>

                    <main className="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                        <Outlet/>   {/*'Nested Router가 여기서 교체될 것이다' */}
                    </main>
                </div>
            </div>
        </div>



    );
}

export default AdminMain;