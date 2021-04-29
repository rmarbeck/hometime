nbMessages = 0
messageDisplayDelayInMS = 5000
currentDelayForRetryingWebSocketInMs = 0
defaultDelayForRetryingWebSocketInMs = 5000
maxDelayForRetryingWebSocketInMs = 60000
today = new Date(new Date().setHours(0,0,0,0))
protocol = "wss://"
$ ->
  if (location.protocol != 'https:')
   protocol="ws://"
  initWS()

initWS = () ->
    console.log("Initing WebSocket")
    setInterval(myTimer, messageDisplayDelayInMS)
    ws = new WebSocket protocol+location.host+$("body").data("ws-url")
    ws.onopen = () ->
        ws.send(JSON.stringify({starting: true}))
        $("#dialog").addClass("hidden");
        currentDelayForRetryingWebSocketInMs = 0
    ws.onerror = () ->
        console.log("error in message found")
        console.log(event)
        $("#dialog").removeClass("hidden")
        ws.close()
    ws.onclose = () ->
        console.log("closing web socket")
        console.log(event)
        $("#dialog").removeClass("hidden")
        setTimeout () ->
          initWS()
        , getDelayForRetry()
    ws.onmessage = (event) ->
      message = JSON.parse event.data
      switch message.type
        when "orderRequest"
          populateOrderRequest(message)
        when "appointments"
          populateAppointments(message)
        when "customerWatchesAllocated"
          populateCWatchesAllocated(message)
        when "customerWatchesQuickWins"
          populateCWatchesQuickWins(message)
        when "customerWatchesEmergencies"
          populateCWatchesEmergency(message)
        when "customerWatchesUnderWaranty"
          populateCWatchesUnderWaranty(message)
        when "spareParts"
          populateSpareParts(message)
        when "statistics"
          populateStats(message)
        when "internalMessages"
          populateInternalMessages(message)
        else
          if(message.ping)
            console.log("PING")
          else
            console.log("not managed")
            console.log(message)

getDelayForRetry = () ->
    if(currentDelayForRetryingWebSocketInMs >= maxDelayForRetryingWebSocketInMs)
       currentDelayForRetryingWebSocketInMs = maxDelayForRetryingWebSocketInMs
    else
       currentDelayForRetryingWebSocketInMs = currentDelayForRetryingWebSocketInMs + defaultDelayForRetryingWebSocketInMs
    currentDelayForRetryingWebSocketInMs

preparingDisplay = (cssid, JsonTab) ->
    $("#"+cssid+" .cloned").remove()
    if(JSON.parse(JsonTab).length == 0)
        $("#"+cssid).addClass("hidden")
    else
        $("#"+cssid).removeClass("hidden")

cloneRow = (cssid) ->
    return $("#"+cssid+" #ph_line_template").clone()

getValueInDictionnary = (dictionary, key) ->
    $("#data_"+dictionary+"_dic [data_key="+key+"]").attr("data_value")

pushClonedRow = (clonedRow, cssid, itemId) ->
    $(clonedRow).attr("id", "#"+cssid+"_"+itemId)
    $(clonedRow).addClass("cloned")
    $(clonedRow).removeClass("hidden")
    clonedRow.appendTo("#"+cssid+" tbody")

populateOrderRequest = (message) ->
    preparingDisplay("order_requests", message.orderRequest)
    $.each( JSON.parse(message.orderRequest), (i, item) ->
     clonedRow = cloneRow("order_requests")
     $("td.ph_id", clonedRow).html('#'+item.id)
     $("td.ph_date", clonedRow).html(item.date)
     $("td.ph_details", clonedRow).html(item.brand+", "+item.model+", "+item.city)
     $("td.ph_name", clonedRow).html(item.managedBy)
     pushClonedRow(clonedRow, "order_requests", item.id))

populateAppointments = (message) ->
    preparingDisplay("appointments", message.appointments)
    $.each( JSON.parse(message.appointments), (i, item) ->
     clonedRow = cloneRow("appointments")
     $("td.ph_date", clonedRow).html(item.date)
     $("td.ph_details", clonedRow).html(item.name)
     $("td.ph_reason", clonedRow).html(getValueInDictionnary("reason",item.reason))
     $("td.ph_status", clonedRow).html(getValueInDictionnary("status",item.status))
     $(clonedRow).addClass("appointment_status_"+item.status)
     pushClonedRow(clonedRow, "appointments", item.id))

populateCWatchesAllocated = (message) ->
    preparingDisplay("managed", message.customerWatchesAllocated)
    $.each( JSON.parse(message.customerWatchesAllocated), (i, item) ->
     clonedRow = cloneRow("managed")
     $("td.ph_id", clonedRow).html('#'+item.id)
     $("td.ph_customer", clonedRow).html(item.nameOfCustomer)
     $("td.ph_brand", clonedRow).html(item.brand)
     $("td.ph_model", clonedRow).html(item.model)
     $("td.ph_watchmaker", clonedRow).html(item.managedBy)
     $("td.ph_status", clonedRow).html(item.status+"%")
     $(clonedRow).addClass("nosolution_status_"+item.noSolution)
     $(clonedRow).addClass("needhelp_status_"+item.needHelp)
     $(clonedRow).addClass("sparetofind_status_"+item.sparepartToFind)
     $(clonedRow).addClass("sparefound_status_"+item.sparepartFound)
     pushClonedRow(clonedRow, "managed", item.id))

populateCWatches = (cssid, JsonTab) ->
    preparingDisplay(cssid, JsonTab)
    $.each( JSON.parse(JsonTab), (i, item) ->
     clonedRow = cloneRow(cssid)
     $("td.ph_id", clonedRow).html('#'+item.id)
     $("td.ph_customer", clonedRow).html(item.nameOfCustomer)
     $("td.ph_brand", clonedRow).html(item.brand)
     $("td.ph_model", clonedRow).html(item.model)
     $("td.ph_due_date", clonedRow).html(item.firstKnownDate)
     if(item.hasConstraint)
        $("td.ph_due_date", clonedRow).prepend('<span class="glyphicon glyphicon-warning-sign"></span> ')
        $(clonedRow).addClass("customer_has_constraint_true")
     if(item.hasCalledForDelay)
        $("td.ph_due_date", clonedRow).prepend('<span class="glyphicon glyphicon-earphone"></span> ')
        $(clonedRow).addClass("customer_has_called_true")
     if(item.enteredUnderWaranty is true and item.toWorkOnUnderWaranty is false)
        $(clonedRow).addClass("to_check_under_waranty")
     pushClonedRow(clonedRow, cssid, item.id))

manageOverFlow = (cssid, size, maxSizeColOne) ->
    $('.overflow').remove()
    if(size > maxSizeColOne)
      newDiv = $('#'+cssid+' div[class^=col-lg]').clone()
      newDiv.addClass("overflow")
      $(' table > tbody > tr', newDiv).each( (i, row) ->
        if (i < maxSizeColOne)
          row.remove())
      $('#'+cssid+' div[class^=col-lg] table > tbody > tr').each( (i, row) ->
        if (i >= maxSizeColOne)
          row.remove())
      newDiv.appendTo("#"+cssid)
      $('#'+cssid+' div[class^=col-lg]').addClass("col-lg-6")
      $('#'+cssid+' div[class^=col-lg]').removeClass("col-lg-12")
    else
      $('#'+cssid+' div[class^=col-lg]').addClass("col-lg-12")
      $('#'+cssid+' div[class^=col-lg]').removeClass("col-lg-6")

populateCWatchesQuickWins = (message) ->
    populateCWatches("quick_wins", message.customerWatchesQuickWins)
    
populateCWatchesUnderWaranty = (message) ->
    populateCWatches("sav", message.customerWatchesUnderWaranty)

populateCWatchesEmergency = (message) ->
    populateCWatches("emergencies", message.customerWatchesEmergencies)
    populateCWatches("emergencies_full", message.customerWatchesEmergencies)
    manageOverFlow("emergencies_full", JSON.parse(message.customerWatchesEmergencies).length, 30)

populateSpareParts = (message) ->
    preparingDisplay("spare_parts", message.spareParts)
    $.each( JSON.parse(message.spareParts), (i, item) ->
     clonedRow = cloneRow("spare_parts")
     $("td.ph_id", clonedRow).html('#'+item.id)
     $("td.ph_watch", clonedRow).html(item.watch)
     $("td.ph_description", clonedRow).html(item.description)
     $("td.ph_reference", clonedRow).html(item.reference)
     pushClonedRow(clonedRow, "spare_parts", item.id))

populateStats = (message) ->
    $("span#stats").html("")
    $.each( JSON.parse(message.statistics), (i, item) ->
     if(item.toQuote != 0)
       $("span#stats").append(" - "+item.toQuote+" devis")
     if(item.toSend != 0)
       $("span#stats").append(" - "+item.toSend+" à envoyer")
     if(item.toTest != 0)
       $("span#stats").append(" - "+item.toTest+" en test"))

populateInternalMessages = (message) ->
    preparingDisplay("display_messages", message.internalMessages)
    $("div#internal_messages").html("")
    if(JSON.parse(message.internalMessages).length == 0)
      $("div#no-messages").removeClass("hidden")
      $("div#display_messages").removeClass("hidden")
    else
      $("div#no-messages").addClass("hidden")
    $.each( JSON.parse(message.internalMessages), (i, item) ->
      $("div#internal_messages").append('<h4><span id="content-'+i+'"><span class="glyphicon glyphicon-'+getValueInDictionnary("internal_messages",item.type)+'"></span> '+item.body+'</span></h4>'))
    displayMessages()

displayMessages = () ->
    nbMessages = $("div#internal_messages span[id^=content]").length
    today = new Date(new Date().setHours(0,0,0,0))
    myTimer()

myTimer = () ->
    if(nbMessages!=0)
      millis = new Date() - today; 
      indice = Math.floor(millis / messageDisplayDelayInMS) % nbMessages
      textvalue = $('#content-'+indice).html()
      $('.message-holder').html(" - "+textvalue)
