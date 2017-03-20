package servlets;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class RepaymentSchedule{
	
	private double cash;
	private int quantityOfPayment;
	private double interest;
	private double constPayment;
	private boolean constInstallment;
	private boolean toPdf;
	
	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;
	
	public RepaymentSchedule(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		httpServletRequest = request;
		httpServletResponse = response;
		
		if(checkData(httpServletRequest)){
			
			cash = Double.parseDouble(httpServletRequest.getParameter("cash"));
			quantityOfPayment = Integer.parseInt(httpServletRequest.getParameter("quantityOfPayment"));
			interest = Double.parseDouble(httpServletRequest.getParameter("interest"));
			
			String checkString = httpServletRequest.getParameter("constPayment");
			
			if(!(checkString == null || checkString.equals(""))){
				
				constPayment = Double.parseDouble(httpServletRequest.getParameter("constPayment"));
			}
			
			// kindOfInstallment - false dla decreasing, true dla const
			if(httpServletRequest.getParameter("kindOfInstallment").equals("const")){
				
				constInstallment = true;
			}
			else{
				
				constInstallment = false;
			}
			
			if(httpServletRequest.getParameter("przycisk").equals("Wyœwietl")){
				
				toPdf = false;
			}else{
				
				toPdf = true;
			}
		}
		else{
			
			httpServletResponse.sendRedirect("/");
		}
	}
	
	public boolean checkData(HttpServletRequest httpServletRequest){
		
		String checkString;
		
		checkString = httpServletRequest.getParameter("cash");
		if(checkString == null || checkString.equals("")) return false;
		
		checkString = httpServletRequest.getParameter("quantityOfPayment");
		if(checkString == null || checkString.equals("")) return false;
		
		checkString = httpServletRequest.getParameter("interest");
		if(checkString == null || checkString.equals("")) return false;
		
		checkString = httpServletRequest.getParameter("kindOfInstallment");
		if(checkString == null || checkString.equals("")) return false;
		
		return true;
	}
	
	public void showTable() throws IOException, DocumentException{
		
		if(toPdf){
			
			generatePdf(constInstallment);
			httpServletResponse.sendRedirect("/");
		}
		
		String webContent = "";
		
		webContent += "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">";
		
		webContent += "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>Kredyt</title></head><body>"
				+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		
		webContent += "<table border = 2><tr><td>Miesiac</td><td>Saldo poczatkowe kapitalu</td>"
				+ "<td>Splata odsetek</td><td>Splata kredytu</td><td>Pelna rata</td>"
				+ "<td>Pozostalo do splaty</td></tr>";
		
		double balance = cash;
		double r = (double) interest / 100 / 12;
		double installment;
		double interest;
		
		if(constInstallment){
			
			installment = (balance * r * Math.pow(r + 1, quantityOfPayment)) / (Math.pow(r + 1, quantityOfPayment) - 1);
			
			for(int i = 1; i < quantityOfPayment; i++){
				
				interest = balance * r;
				
				webContent += "<tr><td>" + i + "</td>"
						+ "<td>" + String.format("%.2f", balance) + "</td>"
						+ "<td>" + String.format("%.2f", interest) + "</td>"
								+ "<td>" + String.format("%.2f", installment - interest) + "</td>";
				
				balance = balance - installment + interest;
				
				webContent += "<td>" + String.format("%.2f", installment) + "</td>"
						+ "<td>" + String.format("%.2f", balance) + "</td></tr>";
			}
		}
		else{
			
			installment = (balance * (1 + (quantityOfPayment) * r)) / (quantityOfPayment);
			installment -= (balance * r);
			
			for(int i = 1; i < quantityOfPayment; i++){
				
				interest = balance * r;
				
				webContent += "<tr><td>" + i + "</td>"
						+ "<td>" + String.format("%.2f", balance) + "</td>"
						+ "<td>" + String.format("%.2f", interest) + "</td>"
								+ "<td>" + String.format("%.2f", installment) + "</td>";
				
				balance = balance - installment;
				
				webContent += "<td>" + String.format("%.2f", installment + interest) + "</td>"
						+ "<td>" + String.format("%.2f", balance) + "</td></tr>";
			}
		}
		
		interest = balance * r;
		
		webContent += "<tr><td>" + quantityOfPayment + "</td>"
				+ "<td>" + String.format("%.2f", balance) + "</td>"
				+ "<td>" + String.format("%.2f", interest) + "</td>"
						+ "<td>" + String.format("%.2f", balance - interest) + "</td>"
						+ "<td>" + String.format("%.2f", balance) + "</td><td>" + 0 + "</td></tr>"
								+ "</table></body></html>";
		
		httpServletResponse.getWriter().println(webContent);
		httpServletResponse.setContentType("text/html");
	}
	
	private void generatePdf(boolean constInstallment) throws FileNotFoundException, DocumentException{
		
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(System.getProperty("user.home") + "/Desktop/harmonogram.pdf"));
		document.open();
		addContentPdf(document);
		document.close();
	}
	
	private void addContentPdf(Document document) throws DocumentException{
		
		Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
                Font.BOLD);
		
		Paragraph preface = new Paragraph();
		preface.add(new Paragraph(
                "Wygenerowano przez: " + System.getProperty("user.name") + ", " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                catFont));
		preface.add(new Paragraph(" "));
		preface.add(new Paragraph("Harmonogram splat kredytu", catFont));
		preface.add(new Paragraph(" "));
		
       	document.add(preface);
        
        PdfPTable table = new PdfPTable(6);
        
        table.addCell(new PdfPCell(new Phrase("Miesiac")));
        table.addCell(new PdfPCell(new Phrase("Saldo poczatkowe kapitalu")));
        table.addCell(new PdfPCell(new Phrase("Splata odsetek")));
        table.addCell(new PdfPCell(new Phrase("Splata kredytu")));
        table.addCell(new PdfPCell(new Phrase("Pelna rata")));
        table.addCell(new PdfPCell(new Phrase("Pozostalo do splaty")));
        
		double balance = cash;
		double r = (double) interest / 100 / 12;
		double installment;
		double interest;
        
		if(constInstallment){
			
			installment = (balance * r * Math.pow(r + 1, quantityOfPayment)) / (Math.pow(r + 1, quantityOfPayment) - 1);
			
			for(int i = 1; i < quantityOfPayment; i++){
				
				interest = balance * r;
				
				table.addCell(new PdfPCell(new Phrase(new String("" + i))));
				table.addCell(new PdfPCell(new Phrase(String.format("%.2f", balance))));
				table.addCell(new PdfPCell(new Phrase(String.format("%.2f", interest))));
				table.addCell(new PdfPCell(new Phrase(String.format("%.2f", installment - interest))));
				
				balance = balance - installment + interest;
				
				table.addCell(new PdfPCell(new Phrase(String.format("%.2f", installment))));
				table.addCell(new PdfPCell(new Phrase(String.format("%.2f", balance))));
			}
		}
		else{
			
			installment = (balance * (1 + (quantityOfPayment) * r)) / (quantityOfPayment);
			installment -= (balance * r);
			
			for(int i = 1; i < quantityOfPayment; i++){
				
				interest = balance * r;
				
				table.addCell(new PdfPCell(new Phrase(new String("" + i))));
				table.addCell(new PdfPCell(new Phrase(String.format("%.2f", balance))));
				table.addCell(new PdfPCell(new Phrase(String.format("%.2f", interest))));
				table.addCell(new PdfPCell(new Phrase(String.format("%.2f", installment))));
				
				balance = balance - installment;
				
				table.addCell(new PdfPCell(new Phrase(String.format("%.2f", installment + interest))));
				table.addCell(new PdfPCell(new Phrase(String.format("%.2f", balance))));
			}
		}
		
		interest = balance * r;
		
		table.addCell(new PdfPCell(new Phrase(new String("" + quantityOfPayment))));
		table.addCell(new PdfPCell(new Phrase(String.format("%.2f", balance))));
		table.addCell(new PdfPCell(new Phrase(String.format("%.2f", interest))));
		table.addCell(new PdfPCell(new Phrase(String.format("%.2f", balance - interest))));
		table.addCell(new PdfPCell(new Phrase(String.format("%.2f", balance))));
		table.addCell(new PdfPCell(new Phrase("0")));
		
        document.add(table);
	}
	
	public double getCash() {
		return cash;
	}

	public void setCash(double cash) {
		this.cash = cash;
	}

	public int getQuantityOfPayment() {
		return quantityOfPayment;
	}

	public void setQuantityOfPayment(int quantityOfPayment) {
		this.quantityOfPayment = quantityOfPayment;
	}

	public double getInterest() {
		return interest;
	}

	public void setInterest(double interest) {
		this.interest = interest;
	}

	public double getConstPayment() {
		return constPayment;
	}

	public void setConstPayment(double constPayment) {
		this.constPayment = constPayment;
	}

	public boolean isConstInstallment() {
		return constInstallment;
	}

	public void setConstInstallment(boolean constInstallment) {
		this.constInstallment = constInstallment;
	}

	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}

	public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
	}

	public HttpServletResponse getHttpServletResponse() {
		return httpServletResponse;
	}

	public void setHttpServletResponse(HttpServletResponse httpServletResponse) {
		this.httpServletResponse = httpServletResponse;
	}
}