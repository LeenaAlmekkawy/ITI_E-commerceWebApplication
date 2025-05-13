<c:if test="${empty sessionScope.adminID}">
    <c:redirect 
    url="admin_login.jspx?errorMessage=Not Logged In or Session Timed Out!"
    />
</c:if>