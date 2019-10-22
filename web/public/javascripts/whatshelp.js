
	var isSafari = /^((?!chrome|android).)*safari/i.test(navigator.userAgent);

    function wh() {
        var options = {
        	email: "wcontact@@hometime.fr", // Email
            sms: "+33782434751", // Sms phone number
            call: "+33782434751", // Call phone number
            whatsapp: "+33782434751", // Call phone number
            call_to_action: "Contactez nous !", // Call to action
            button_color: "#CC3333", // Color of button
            position: "left", // Position may be 'right' or 'left'
            order: "email,whatsapp,call,sms", // Order of buttons
        };
        var proto = document.location.protocol, host = "whatshelp.io", url = proto + "//static." + host;
        var s = document.createElement('script'); s.type = 'text/javascript'; s.async = true; s.src = url + '/widget-send-button/js/init.js';
        s.onload = function () { WhWidgetSendButton.init(host, proto, options); };
        var x = document.getElementsByTagName('script')[0]; x.parentNode.insertBefore(s, x);
    };
    
    if (!isSafari) { 
    	wh();
    }
    
