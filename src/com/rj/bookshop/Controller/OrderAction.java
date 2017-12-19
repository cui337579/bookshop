package com.rj.bookshop.Controller;

import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rj.bookshop.Entity.Order;
import com.rj.bookshop.Entity.OrderLine;
import com.rj.bookshop.Entity.User;
import com.rj.bookshop.Service.OrderService;
import com.rj.bookshop.Service.UserService;

@Controller
public class OrderAction {
	@Autowired
	private OrderService orderService;
	@Autowired
	private UserService userService;
	
	@RequestMapping("/addorder")
	public String addOrder(HttpSession session,
			@RequestParam("uid") String uid,
			@RequestParam("bid") String bid) {
		if(uid.equals("")) {
			return "login";
		}
		int userid = Integer.parseInt(uid);
		int bookid = Integer.parseInt(bid);
		orderService.addOrder(userid, bookid);
		return "index";
	}
	
	@RequestMapping("/deleteorder")	
	public String deleteOrder(HttpSession session,
			@RequestParam String orderlineid) {
			int oli = Integer.parseInt(orderlineid);
			User user = (User) session.getAttribute("user");
		
			Set<Order> orderSet = user.getOrderSet();
			for(Order o:orderSet) {
				for (OrderLine ol:o.getOrderlineSet()) {
					if(ol.getOrderline_id() == oli) {
						o.getOrderlineSet().remove(ol);
					}
				}
			}
			orderService.deleteOrder(orderService.selectOrderLineById(oli));
			orderSet = user.getOrderSet();
			session.setAttribute("orderset", orderSet);
			
			double price = 0;		
			for(Order o:orderSet) {
				for (OrderLine ol:o.getOrderlineSet()) {
					price = price+ol.getPrice();
				}
			}
			
			session.setAttribute("price", price);
			
		return "ordershow";
	}
	
	
	@RequestMapping("/ordershow")
	public String selectOrder(HttpSession session,
			@RequestParam("user_id") String user_id) {
		if(user_id.equals("")) {
			session.setAttribute("errorMsg", "请先登录");
			return "login";
		}else {
			int uid = Integer.parseInt(user_id);
			User user = userService.selectUserById(uid);
			
			Set<Order> orderSet = user.getOrderSet();
		
			double price = 0;		
			for(Order o:orderSet) {
				if(o.getState()==1) {
					for (OrderLine ol:o.getOrderlineSet()) {
						price = price+ol.getPrice();
					}
				}else {
					orderSet.remove(o);
				}
			}
			session.setAttribute("orderset", orderSet);
			session.setAttribute("price", price);
			return "ordershow";
		}
	}
	
	@RequestMapping("/ordersubmit")
	public String submitOrder(HttpSession session) {
			User user = (User) session.getAttribute("user");
			
			Set<Order> orderSet = user.getOrderSet();
			for(Order o:orderSet) {
				o.setState(0);
				orderService.updateOrder(o);
			}
			session.setAttribute("orderset", orderSet);

			return "index";		
	}
	
}
