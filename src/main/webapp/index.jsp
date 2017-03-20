<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Kredyt</title>
</head>
<body>

	<h1>Program do generowania harmonogramu spłat kredytu</h1>

	<form action="hello" method="post">
		<table border="2">
			<tr>
				<td>Wnioskowana kwota kredytu: <input type="number" name="cash" min="1"/></td>
			</tr>
			<tr>
				<td>Ilość rat: <input type="number" name="quantityOfPayment" min="2"/></td>
			</tr>
			<tr>
				<td>Oprocentowanie: <input type="number" name="interest" min="0"/></td>
			</tr>
			<tr>
				<td>Opłata stała: <input type="number" name="constPayment" min="0"/></td>
			</tr>
				<td>Rodzaj rat:
					<input type="radio" name="kindOfInstallment" value="decreasing"/> malejąca
					<input type="radio" name="kindOfInstallment" value="const" checked="checked"/> stała
				</td>
			<tr>
			<tr>
				<td>
					<input type="submit" value="Wyświetl" name="przycisk"/>
					<input type="submit" value="Wygeneruj do PDF" name="przycisk"/>
				</td>
			</tr>
		</table>
	</form>
</body>
</html>