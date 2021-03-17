
$ ->
  protocol = "wss://"
  if (location.protocol != 'https:')
   protocol="ws://"
  ws = new WebSocket protocol+location.host+$("body").data("ws-url")
  ws.onopen = () ->
        console.log(event)
        ws.send(JSON.stringify({starting: true}))
  ws.onmessage = (event) ->
    message = JSON.parse event.data
    switch message.type
      when "orderRequest"
        populateOrderRequest(message)
      when "appointments"
        populateAppointments(message)
      when "customerWatchesAllocated"
        populateCWatchesAllocated(message)
      else
        console.log(message)


populateOrderRequest = (message) ->
    console.log("updating Order requests")
    $("#order_requests .cloned").remove()
    if (JSON.parse(message.orderRequest).length == 0)
     $("#order_requests").addClass("hidden")
    $.each( JSON.parse(message.orderRequest), (i, item) ->
     $("#order_requests").removeClass("hidden")
     clonedRow = $("#order_requests #ph_line_template").clone()
     $("td.ph_id", clonedRow).html('#'+item.id)
     $("td.ph_date", clonedRow).html(item.date)
     $("td.ph_details", clonedRow).html(item.brand+", "+item.model+", "+item.city)
     $("td.ph_name", clonedRow).html(item.managedBy)
     $(clonedRow).attr("id", "#order_"+item.id)
     $(clonedRow).addClass("cloned")
     $(clonedRow).removeClass("hidden")
     clonedRow.appendTo("#order_requests tbody"))


populateAppointments = (message) ->
    console.log("updating appointments")
    $("#appointments .cloned").remove()
    if (JSON.parse(message.appointments).length == 0)
     $("#appointments").addClass("hidden")
    $.each( JSON.parse(message.appointments), (i, item) ->
     $("#appointments").removeClass("hidden")
     clonedRow = $("#appointments #ph_line_template").clone()
     $("td.ph_date", clonedRow).html(item.date)
     $("td.ph_details", clonedRow).html(item.name)
     $("td.ph_reason", clonedRow).html(item.reason)
     $("td.ph_status", clonedRow).html(item.status)
     $(clonedRow).attr("id", "#appointment_"+item.id)
     $(clonedRow).addClass("cloned")
     $(clonedRow).removeClass("hidden")
     clonedRow.appendTo("#appointments tbody"))
