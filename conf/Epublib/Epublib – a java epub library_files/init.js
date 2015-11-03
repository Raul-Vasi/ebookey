jQuery(function ($) {

	$(".menu-icon").click(function(){
		$(".nav-primary ul.menu-primary").slideToggle();
		$(".menu-icon").toggleClass( "active" );
	});


	$(window).resize(function(){
		if(window.innerWidth > 768) {
			$(".nav-primary ul.menu-primary, nav .sub-menu").removeAttr("style");
		}
	});

});