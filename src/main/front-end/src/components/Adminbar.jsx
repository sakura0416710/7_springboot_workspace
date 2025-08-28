import './Adminbar.css';

const Adminbar = () =>{
   
   
    return(
    <div className="sidebar border border-right col-md-3 col-lg-2 p-0 bg-body-tertiary">
		<div className="offcanvas-lg offcanvas-end bg-body-tertiary" tabIndex="-1" id="sidebarMenu" aria-labelledby="sidebarMenuLabel">
			<div className="offcanvas-body d-md-flex flex-column p-0 pt-lg-3 overflow-y-auto">
				<ul className="nav flex-column">
					<li className="nav-item">
						<a className="nav-link d-flex align-items-center gap-2 active" aria-current="page" href="/admin/home"> 
							<svg className="bi">
								<use xlinkHref="#house-fill" />
							</svg> Dashboard
						</a>
					</li>
					<li className="nav-item">
						<a className="nav-link d-flex align-items-center gap-2" href="/admin/members"> 
							<svg className="bi">
								<use xlinkHref="#people" />
							</svg> Members
						</a>
					</li>
					<li className="nav-item">
						<a className="nav-link d-flex align-items-center gap-2" href="/admin/boards">
							<svg className="bi">
								<use xlinkHref="#file-earmark-text" />
							</svg> General Board
						</a>
					</li>
					<li className="nav-item">
						<a className="nav-link d-flex align-items-center gap-2" href="/admin/attms">
							<svg className="bi">
								<use xlinkHref="#puzzle" />
							</svg> Attachment Board
						</a>
					</li>
				</ul>
				<hr className="my-3"></hr>
				<ul className="nav flex-column mb-auto">
					<li className="nav-item">
						<a className="nav-link d-flex align-items-center gap-2" href="/admin/logout"> 
							<svg className="bi">
								<use xlinkHref="#door-closed" />
							</svg> Sign out
						</a>
					</li>
				</ul>
			</div>
		</div>
	</div>
    );
}

export default Adminbar;