
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
      when "unreplied"
        populateUnreplied(message)
      when "appointments"
        populateAppointments(message)
      else
        console.log(message)


populateUnreplied = (message) ->
	$("#unreplied .cloned").remove()
	if (JSON.parse(message.unmanaged).length == 0)
     $("#unreplied").addClass("hidden")
	$.each( JSON.parse(message.unmanaged), (i, item) ->
     $("#unreplied").removeClass("hidden")
     clonedRow = $("#unreplied #ph_line_template").clone()
     $("td.ph_id", clonedRow).html('#'+item.id)
     $("td.ph_date", clonedRow).html(item.date)
     $("td.ph_details", clonedRow).html(item.brand+", "+item.model+", "+item.city)
     $("td.ph_name", clonedRow).html(item.managedBy)
     $(clonedRow).attr("id", "#order_"+item.id)
     $(clonedRow).addClass("cloned")
     $(clonedRow).removeClass("hidden")
     clonedRow.appendTo("#unreplied tbody"))


populateAppointments = (message) ->
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
