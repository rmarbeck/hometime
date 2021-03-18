
$ ->
  protocol = "wss://"
  if (location.protocol != 'https:')
   protocol="ws://"
  initWS(protocol)

initWS = (protocol) ->
    ws = new WebSocket protocol+location.host+$("body").data("ws-url")
    ws.onopen = () ->
        console.log(event)
        ws.send(JSON.stringify({starting: true}))
        $("#dialog").addClass("hidden");
    ws.onerror = () ->
        console.log(event)
        $("#dialog").removeClass("hidden");
        ws.close()
    ws.onclose = () ->
        console.log(event)
        $("#dialog").removeClass("hidden");
        setTimeout () ->
          initWS(protocol)
        , 5000
    ws.onmessage = (event) ->
      message = JSON.parse event.data
      console.log(message)
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
        else
          console.log("not managed")

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
    console.log("populating "+cssid)
    console.log(JsonTab)
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
     if(item.hasCalledForDelay)
        $("td.ph_due_date", clonedRow).prepend('<span class="glyphicon glyphicon-earphone"></span> ')
     pushClonedRow(clonedRow, cssid, item.id))

populateCWatchesQuickWins = (message) ->
    populateCWatches("quick_wins", message.customerWatchesQuickWins)

populateCWatchesEmergency = (message) ->
    populateCWatches("emergencies", message.customerWatchesEmergencies)