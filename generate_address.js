function generate_address( username, domain ) {
	var atsign = "&#64;";
	var addr = username + atsign + domain;
	document.write("(<" + "a" + " " + "href=" + "mai" + "lto:" + addr + ">"
			+ addr + "<\/a>)");
} 

function generate_contact_address( username, domain ) {
	var atsign = "&#64;";
	var addr = username + atsign + domain;
	document.write("<" + "a" + " class=topmenu " + "href=" + "mai" + "lto:" + addr + ">"
			+ "Contact" + "<\/a>");
}
