package routers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import http.RequestHttp;
import http.ResponseHttp;
import services.InvoiceServices;
import utilityclasses.HttpCodes;
import utilityclasses.HttpMethods;

public class RouterInvoice implements RouterInterface {

	private Map<Pattern, MethodRouter> routers = new HashMap<>();
	private Map<HttpMethods, ArrayList<Pattern>> httpPatterns = new HashMap<>();
	private String datePattern = "(\\d{2}-\\d{2}-\\d{4})";
	private String idPattern = "(\\d+)";

	public RouterInvoice() {

		routers.put(Pattern.compile("/invoice/" + idPattern), this::getInvoice);
		routers.put(Pattern.compile("/invoice/all/" + idPattern), this::getAllInvoices);
		routers.put(Pattern.compile("/invoice/newInvoice/" + idPattern + "/data\\?inicio=" + datePattern + "&fim=" + datePattern),
				this::createInvoice);

		ArrayList<Pattern> patterns = new ArrayList<>();

		patterns.add(Pattern.compile("/invoice/" + idPattern));
		patterns.add(Pattern.compile("/invoice/all/" + idPattern));
		httpPatterns.put(HttpMethods.GET, patterns);
		patterns = new ArrayList<>();
		patterns.add(Pattern.compile("/invoice/newInvoice/" + idPattern + "/data\\?inicio=" + datePattern + "&fim=" + datePattern));
		httpPatterns.put(HttpMethods.POST, patterns);

	}

	public boolean verifyPath(String path) {

		for (Entry<Pattern, MethodRouter> pattern : routers.entrySet()) {
			
			if (pattern.getKey().matcher(path).matches()) {

				return true;

			}

		}

		return false;

	}

	public Pattern verifyPathInHttpMethod(HttpMethods httpMethod, String path) {

		for (Pattern pattern : httpPatterns.get(httpMethod)) {

			if (pattern.matcher(path).matches()) {

				return pattern;

			}

		}

		return null;

	}

	public String execMethodRouter(RequestHttp http, Pattern patternCurrent) {

		MethodRouter methodReq = null;

		for (Entry<Pattern, MethodRouter> methodRouter : routers.entrySet()) {

			if (patternCurrent.pattern().equals(methodRouter.getKey().pattern())) {

				methodReq = methodRouter.getValue();

			}

		}

		return methodReq.method(http);

	}

	@Override
	public String router(RequestHttp http) {

		Pattern pattern;
		String responseHttp = null;

		if (http.getMethod() == HttpMethods.GET) {

			if (verifyPath(http.getPath())) {

				if ((pattern = verifyPathInHttpMethod(HttpMethods.GET, http.getPath())) != null) {

					responseHttp = execMethodRouter(http, pattern);

				} else {

					Map<String, String> mapHeaders = new HashMap<>();
					mapHeaders.put("Allow", "POST");
					mapHeaders.put("Content-Length", "0");
					responseHttp = new ResponseHttp(HttpCodes.HTTP_405.getCodeHttp(), mapHeaders).toString();

				}

			} else {

				Map<String, String> mapHeaders = new HashMap<>();
				mapHeaders.put("Content-Length", "0");
				responseHttp = new ResponseHttp(HttpCodes.HTTP_404.getCodeHttp(), mapHeaders).toString();

			}

		} else if (http.getMethod() == HttpMethods.POST) {
			
			if (verifyPath(http.getPath())) {
				
				if ((pattern = verifyPathInHttpMethod(HttpMethods.POST, http.getPath())) != null) {

					responseHttp = execMethodRouter(http, pattern);

				} else {

					Map<String, String> mapHeaders = new HashMap<>();
					mapHeaders.put("Allow", "GET");
					mapHeaders.put("Content-Length", "0");
					responseHttp = new ResponseHttp(HttpCodes.HTTP_405.getCodeHttp(), mapHeaders).toString();

				}

			} else {

				Map<String, String> mapHeaders = new HashMap<>();
				mapHeaders.put("Content-Length", "0");
				responseHttp = new ResponseHttp(HttpCodes.HTTP_404.getCodeHttp(), mapHeaders).toString();

			}

//		} else if (http.getMethod() == HttpMethods.PUT) {
//
//			if (verifyPath(http.getPath())) {
//
//				if ((pattern = verifyPathInHttpMethod(HttpMethods.PUT, http.getPath())) != null) {
//
//					execMethodRouter(http, pattern);
//
//				} else {
//
//					// Erro
//
//				}
//
//			} else {
//
//				// Erro
//
//			}
//
//		} else if (http.getMethod() == HttpMethods.DELETE) {
//
//			if (verifyPath(http.getPath())) {
//
//				if ((pattern = verifyPathInHttpMethod(HttpMethods.DELETE, http.getPath())) != null) {
//
//					responseHttp = execMethodRouter(http, pattern);
//
//				} else {
//
//					// Erro
//
//				}
//
//			} else {
//
//				// Erro
//
//			}

		} else {

			Map<String, String> mapHeaders = new HashMap<>();
			mapHeaders.put("Content-Length", "0");
			responseHttp = new ResponseHttp(HttpCodes.HTTP_501.getCodeHttp(), mapHeaders).toString();

		}

		return responseHttp;

	}

	public String getInvoice(RequestHttp http) {

		Pattern pattern = Pattern.compile("/invoice/" + idPattern);
		Matcher matcher = pattern.matcher(http.getPath());
		matcher.matches();
		ResponseHttp response;
		JSONObject jsonRespString = InvoiceServices.getInvoiceJSON(matcher.group(1));
		Map<String, String> mapHeaders = new HashMap<>();

		if (jsonRespString == null) {

			mapHeaders.put("Content-Length", "0");
			response = new ResponseHttp(HttpCodes.HTTP_404.getCodeHttp(), mapHeaders);

		} else {

			mapHeaders.put("Content-Type", "application/json");
			mapHeaders.put("Content-Length", Integer.toString(jsonRespString.toString().getBytes().length));
			response = new ResponseHttp(HttpCodes.HTTP_200.getCodeHttp(), mapHeaders, jsonRespString.toString());

		}

		return response.toString();

	}

	public String getAllInvoices(RequestHttp http) {

		Pattern pattern = Pattern.compile("/invoice/all/" + idPattern);
		Matcher matcher = pattern.matcher(http.getPath());
		matcher.matches();
		ResponseHttp response;
		JSONObject jsonRespString = InvoiceServices.getInvoicesJSON(matcher.group(1));
		Map<String, String> mapHeaders = new HashMap<>();

		if (jsonRespString == null) {

			mapHeaders.put("Content-Length", "0");
			response = new ResponseHttp(HttpCodes.HTTP_404.getCodeHttp(), mapHeaders);

		} else {

			mapHeaders.put("Content-Type", "application/json");
			mapHeaders.put("Content-Length", Integer.toString(jsonRespString.toString().getBytes().length));
			response = new ResponseHttp(HttpCodes.HTTP_200.getCodeHttp(), mapHeaders, jsonRespString.toString());

		}

		return response.toString();

	}

	public String createInvoice(RequestHttp http) {

		Pattern pattern = Pattern.compile("/invoice/newInvoice/" + idPattern + "/data\\?inicio=" + datePattern + "&fim=" + datePattern);
		Matcher matcher = pattern.matcher(http.getPath());
		matcher.matches();
		ResponseHttp response;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		Map<String, String> mapHeaders = new HashMap<>();
		String respMethod = InvoiceServices.addInvoice(matcher.group(1), LocalDate.parse(matcher.group(2), formatter),LocalDate.parse(matcher.group(3), formatter)).toString();

		if (respMethod == null) {

			mapHeaders.put("Content-Length", "0");
			response = new ResponseHttp(HttpCodes.HTTP_404.getCodeHttp(), mapHeaders);

		} else {

			mapHeaders.put("Content-Length", "0");
			mapHeaders.put("Location", "/invoice/" + respMethod);
			response = new ResponseHttp(HttpCodes.HTTP_201.getCodeHttp(), mapHeaders);

		}

		return response.toString();

	}

	public interface MethodRouter {

		public String method(RequestHttp http);

	}

}
