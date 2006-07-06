<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<bean:define id="personId" name="paymentsManagementDTO" property="person.idInternal"/>
<fr:form action="<%="/payments.do?personId=" + personId %>">
	
	<html:hidden name="paymentsForm" property="method" />
	<h2><bean:message key="label.masterDegree.administrativeOffice.payments.preparePayment" /></h2>
	<hr/>
	<br />

	<logic:messagesPresent message="true">
		<ul>
			<html:messages id="messages" message="true">
				<li><span class="error0"><bean:write name="messages" /></span></li>
			</html:messages>
		</ul>
		<br />
	</logic:messagesPresent>
	
	<strong><bean:message key="label.masterDegree.administrativeOffice.payments.person"/>:</strong>
	<fr:view name="paymentsManagementDTO" property="person"
		schema="person.view-with-name-and-idDocumentType-and-documentIdNumber">
		<fr:layout name="tabular">
			<fr:property name="classes" value="tstyle4" />
		</fr:layout>
	</fr:view>
	
	<fr:edit id="paymentsManagementDTO-edit" name="paymentsManagementDTO" schema="paymentsManagementDTO.edit-with-paymentDate">
		<fr:layout name="tabular">
			<fr:property name="classes" value="tstyle4" />
		</fr:layout>
		<fr:destination name="invalid" path="/payments.do?method=preparePaymentInvalid"/>
	</fr:edit>
	
	<table>
		<tr>
			<td>
				<fr:view name="paymentsManagementDTO" property="selectedEntries" schema="entryDTO.view">
					<fr:layout name="tabular" >
						<fr:property name="classes" value="tstyle4"/>
				        <fr:property name="columnClasses" value="listClasses,,"/>
					</fr:layout>
				</fr:view>
			</td>
		</tr>
		<tr>
			<td align="right">
				<strong><bean:message key="label.masterDegree.administrativeOffice.payments.totalAmount"/></strong>:<bean:write name="paymentsManagementDTO" property="totalAmountToPay" />&nbsp;<bean:message key="label.masterDegree.administrativeOffice.payments.currencySymbol"/>
			</td>
		</tr>
	</table>
	
	<html:submit styleClass="inputbutton" onclick="this.form.method.value='doPayment';"><bean:message key="button.masterDegree.administrativeOffice.payments.pay"/></html:submit>
	<html:cancel styleClass="inputbutton" onclick="this.form.method.value='backToShowOperations';"><bean:message key="button.masterDegree.administrativeOffice.payments.back"/></html:cancel>
</fr:form>
