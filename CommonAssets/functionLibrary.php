<? //this page is a list of functions that may be accessed sitewide; listed alphabetically

//prints a javascrpt alert
function alert($foo){
	print "<script type='text/javascript'>alert('$foo')</script>";
} //end alert function


//connect to the database
function connectDB(){
	if (!$who){
	  $user = "karen";
	  $pwd = "davis";
	} //end if
	$db = "Boarding2";
	$conn = mysql_connect('localhost', 'buggdcom_'.$user, $pwd);
	$select = mysql_select_db('buggdcom_'.$db, $conn);
} //end connectDB function


//connect to the rescue database
function connectRescue(){
	$user = "DBcust";
	$pwd = "KL875904";
	$db = "LuckyPaws";
	$conn = mysql_connect('localhost', 'buggdcom_'.$user, $pwd);
	$select = mysql_select_db('buggdcom_'.$db, $conn);
} //end connectRescue Function

function convertToMoney($string){
	if ((preg_match("/\.+/", $string))){
		$thisString = split("\.", $string);
		if (strlen(trim($thisString[1]))<2){
			$string = $thisString[0] . "." . $thisString[1] . "0";
		} else {
			$string = $thisString[0] . "." . $thisString[1];
		} //end if
	} else {
		$string = $string . ".00";
	} //end if
	return $string;
} //end conertToMoney function


//returns the next calendar day in YYYYMMDD format using a date in YYYYMMDD format 					  
function findDate($dateID, $add){
	$mm = substr($dateID,4,2);
	$dd = substr($dateID,6,2);
	$yyyy = substr($dateID,0,4);
	$dd = $dd+$add;
	$i = date("t", mktime(1,1,1,$mm,1,$yyyy));
	while ($dd>$i){
		$dd = $dd - date("t", mktime(1,1,1,$mm,1,$yyyy));
		$mm++;
		if ($mm==13){
			$mm = 1;
			$yyyy++;
		} //end if
	} // end while
	$newDate = $yyyy . date("m", mktime(1,1,1,$mm,$dd,$yyyy)) . date("d", mktime(1,1,1,$mm,$dd,$yyyyy));
	return $newDate;
} //end findDate function


//returns a number of nights using a given start date and end date in YYYYMMDD format
function getDaysNights($start, $end){
  global $nights;
  $MMs = substr($start, 4, 2);
  $DDs = substr($start, 6, 2);
  $YYs = substr($start, 0, 4);
  $MMe = substr($end, 4, 2);
  $DDe = substr($end, 6, 2);
  $YYe = substr($end, 0, 4);
  $startDay = date("z", mktime(0,0,0,$MMs,$DDs,$YYs)); //get the day of the year for start day
  $year = $YYs;
  while ($year!=$YYe){
    $endDay = date("z", mktime(0,0,0,12,31,$year));
    if ($nights[total]){
      $nights[total] = $endDay - $startDay + $nights[total];
    } else {
      $nights[total] = $endDay - $startDay;
    } //end if
    $startDay = -1;
    $year++;
  } //end while
  $endDay = date("z", mktime(0,0,0,$MMe,$DDe,$year));
  if ($nights[total]){
    $nights[total] = $endDay - $startDay + $nights[total];
  } else {
    $nights[total] = $endDay - $startDay;
  } //end if
  //print $nights[total];
  return $nights[total];
} //end getTheNights function

//get the price of a kennel/condo
//$res = array holding inDate, outDate, and created (date reservation was made), all in YYYYMMDD format
//$num = number of animals in kennel
//$type = dog or cat
//$play is whether or not to include daycare with boarding 1=everyday
function priceKennel($res, $num, $type, $play){ 
	//if ($_SERVER['REMOTE_ADDR']=="76.182.116.31"){
	//	$res[created] = 20121102; //testing
	//} //edn if
	$thisDate = $res[inDate];
	while ($thisDate<$res[outDate]){
		//if ($_SERVER['REMOTE_ADDR']=="76.182.116.31"){
		$query = "SELECT * FROM holidays WHERE dateID=$thisDate";
		$hol = mysql_fetch_assoc(mysql_query($query));
		if ($hol){ //this reservation is over a holiday
			$play = 1;
		} //end if
		//} //end if
		$mm = substr($thisDate,4,2);
		$dd = substr($thisDate,6,2);
		$yyyy = substr($thisDate,0,4);
		$query = "SELECT * FROM newPricing WHERE startDate<=$thisDate AND endDate>=$thisDate AND effDate<=$res[created] ORDER BY effDate DESC";
		$price = mysql_fetch_assoc(mysql_query($query));
		if (date("N", mktime(1,1,1,$mm,$dd,$yyyy))>=5) { //use weekend pricing
			$v = $type . "WEnight";
		} else {
			$v = $type . "WDnight";	
		} //end if
		$thisPrice = $price[$v];
		for ($i=2; $i<=$num; $i++){
			$v = $type . "Share";
			$thisPrice = $price[$v] + $thisPrice;
		} //end for
		//print "$thisPrice " . date("N", mktime(1,1,1,$mm,$dd,$yyyy)) . "<br>";
		$totalPrice = $totalPrice + $thisPrice;
		$thisDate = findDate($thisDate, 1);
		$nights++;
	} //end while
	if (($play)&&($type=='dog')){
		$playPrice = $price[play1st]*$nights;
		for ($i=2; $i<=$num; $i++){
			$playPrice = ($price[play2nd]*$nights) + $playPrice;
		} //end for
	} //end if
	if ($nights>=11){ //apply multiple night discount
		$totalPrice = $totalPrice-($totalPrice*$price[multi11p]);
	} else if ($nights>=6){ //apply multiple night discount
		$totalPrice = $totalPrice-($totalPrice*$price[multi6p]);
	} //end if
	$total[boarding] = $totalPrice;
	$total[play] = $playPrice;
	$total[subtotal] = $total[boarding]+$total[playPrice];
	return $total;
} // end priceKennel


//return the date in proper format using an 8-digit date code yyyymmdd
function printDate($dateID){
	$yy = substr($dateID,0,4);
	$mm = substr($dateID,4,2);
	$dd = substr($dateID,6,2);
	$print = $mm . "/" . $dd . "/" . $yy;
	return $print;
} //end printDate function


//displays the time in a nice format
function printTime($timeID){ //display time in a nice format
	$h = substr($timeID,0,2);
	$m = substr($timeID,2,2);
	if ($h>11){
		$ampm = "PM";
		if ($h>12){
			$h = $h-12;
		} //end if
	} else {
		$ampm = "AM";
		if ($h<10){
			$h = substr($h,1,1);
		} //end if
	} //end if
	$theTime = $h . ":" . $m . " " . $ampm;
	return $theTime;
} //end printTime


?>