package tools.vitruv.framework.remote.server;

import java.io.IOException;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

public class AllowAllOriginsFilter extends Filter {

	@Override
	public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
		exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
		exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "DELETE, POST, GET, OPTIONS");
		exchange.getResponseHeaders().add("Access-Control-Allow-Headers",
				"Content-Type, Authorization, X-Requested-With, View-Type, selector-uuid");
		exchange.getResponseHeaders().add("Access-Control-Expose-Headers", "selector-uuid");
		if ("OPTIONS".equals(exchange.getRequestMethod())) {
			exchange.sendResponseHeaders(204, -1);
			return;
		}

		chain.doFilter(exchange);
	}

	@Override
	public String description() {
		return "Allows all origins";
	}

}
