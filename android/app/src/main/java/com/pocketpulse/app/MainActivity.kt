package com.pocketpulse.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

private val Red = Color(0xFFDF3041); private val RedSoft = Color(0xFFFFEDEF); private val Ink = Color(0xFF1D1B24)
private val Ground = Color(0xFFF6F4F5); private val Line = Color(0xFFE9E6E9); private val Green = Color(0xFF178B5E)
private val Radius = RoundedCornerShape(18.dp)

data class Profile(val name:String,val mobile:String,val currency:String,val language:String,val cash:Double,val online:Double)
data class Tx(val id:String,val type:String,val amount:Double,val method:String,val category:String,val note:String,val date:Long)

class MainActivity:ComponentActivity(){override fun onCreate(s:Bundle?){super.onCreate(s);setContent{PocketPulseApp(this)}}}

@Composable private fun PocketPulseApp(context:Context){
 val store=remember{Store(context)}; var profile by remember{mutableStateOf(store.profile())}; var txs by remember{mutableStateOf(store.txs())}
 var screen by rememberSaveable{mutableStateOf(if(profile==null)"welcome" else "home")}; var type by rememberSaveable{mutableStateOf("spent")}
 MaterialTheme { Box(Modifier.fillMaxSize().background(Ground)){when(screen){
  "welcome"->Welcome{p->profile=p;store.saveProfile(p);screen="home"}
  "home"->Home(profile!!,txs,{type="spent";screen="entry"},{type="earned";screen="entry"}){screen=it}
  "entry"->Entry(type,profile!!,{screen="home"}){t->txs=listOf(t)+txs;store.saveTxs(txs);screen="home"}
  "history"->History(profile!!,txs,{screen="home"}){id->txs=txs.filterNot{it.id==id};store.deleteTx(id,txs)}
  "reports"->Reports(profile!!,txs){screen="home"}
  "profile"->ProfilePage(profile!!){screen="home"}
 }}}}

@Composable private fun Welcome(done:(Profile)->Unit){
 var name by rememberSaveable{mutableStateOf("")};var mobile by rememberSaveable{mutableStateOf("")};var cash by rememberSaveable{mutableStateOf("")};var online by rememberSaveable{mutableStateOf("")}
 Column(Modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.SpaceBetween){Column{
  Logo();Spacer(Modifier.height(21.dp));Text("POCKETPULSE",color=Red,fontWeight=FontWeight.Bold,fontSize=12.sp,letterSpacing=1.sp);Spacer(Modifier.height(8.dp));Text("Know where\nyour money goes.",color=Ink,fontSize=32.sp,lineHeight=35.sp,fontWeight=FontWeight.Bold);Spacer(Modifier.height(10.dp));Text("Track cash and online spending in a few seconds.",color=Color(0xFF706D79),fontSize=14.sp)
  Label("What should we call you?");Field(name,"Your name"){name=it};Label("Mobile number");Field(mobile,"Your mobile number"){mobile=it};Label("Starting cash balance");Field(cash,"₹ 0"){cash=it};Label("Starting online balance");Field(online,"₹ 0"){online=it}
 };Button({done(Profile(name.trim(),mobile.trim(),"₹","English",cash.toDoubleOrNull()?:0.0,online.toDoubleOrNull()?:0.0))},enabled=name.isNotBlank()&&mobile.length>=7,modifier=Modifier.fillMaxWidth().height(54.dp),colors=ButtonDefaults.buttonColors(containerColor=Red,disabledContainerColor=Color(0xFFFFBCC4)),shape=RoundedCornerShape(15.dp)){Text("Continue  →",fontWeight=FontWeight.Bold)}}
}
@Composable private fun Label(s:String){Spacer(Modifier.height(13.dp));Text(s,color=Ink,fontWeight=FontWeight.Bold,fontSize=12.sp);Spacer(Modifier.height(6.dp))}
@Composable private fun Field(v:String,h:String,change:(String)->Unit){OutlinedTextField(v,change,Modifier.fillMaxWidth(),singleLine=true,placeholder={Text(h)},shape=RoundedCornerShape(13.dp))}

@Composable private fun Home(p:Profile,txs:List<Tx>,spent:()->Unit,earned:()->Unit,nav:(String)->Unit){
 val cash=p.cash+txs.sumOf{if(it.method=="Cash")if(it.type=="earned")it.amount else -it.amount else 0.0};val online=p.online+txs.sumOf{if(it.method=="Online")if(it.type=="earned")it.amount else -it.amount else 0.0}
 Column(Modifier.fillMaxSize().padding(horizontal=20.dp,vertical=30.dp)){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){Column{Text("POCKETPULSE",color=Color(0xFF706D79),fontWeight=FontWeight.Bold,fontSize=10.sp);Text("Hi, ${p.name} 👋",color=Ink,fontSize=25.sp,fontWeight=FontWeight.Bold)};Logo(true)};Spacer(Modifier.height(22.dp));Balance(p.currency,cash,online);Spacer(Modifier.height(25.dp));Text("What did you do today?",fontWeight=FontWeight.Bold,fontSize=16.sp);Spacer(Modifier.height(10.dp));Action("I Spent","Record money going out",Icons.Outlined.ArrowDownward,RedSoft,Red,spent);Spacer(Modifier.height(9.dp));Action("I Earned","Record money coming in",Icons.Outlined.ArrowUpward,Color(0xFFFFF3CE),Color(0xFF9B6800),earned);Spacer(Modifier.weight(1f));Bottom("home",nav)}
}
@Composable private fun Balance(cur:String,cash:Double,online:Double){Card(colors=CardDefaults.cardColors(containerColor=Red),shape=RoundedCornerShape(24.dp)){Column(Modifier.padding(20.dp)){Text("Total balance",color=Color.White.copy(.8f),fontSize=12.sp);Text(money(cur,cash+online),color=Color.White,fontSize=34.sp,fontWeight=FontWeight.Bold);Spacer(Modifier.height(14.dp));Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Column{Text("💵 Cash",color=Color.White.copy(.8f),fontSize=11.sp);Text(money(cur,cash),color=Color.White,fontWeight=FontWeight.Bold)};Column(horizontalAlignment=Alignment.End){Text("📱 Online",color=Color.White.copy(.8f),fontSize=11.sp);Text(money(cur,online),color=Color.White,fontWeight=FontWeight.Bold)}}}}}

@Composable private fun Entry(type:String,p:Profile,back:()->Unit,save:(Tx)->Unit){
 var amount by rememberSaveable{mutableStateOf("")};var method by rememberSaveable{mutableStateOf("Cash")};var cat by rememberSaveable{mutableStateOf("")};var note by rememberSaveable{mutableStateOf("")};val out=type=="spent";val cats=if(out)listOf("Food","Travel","Shopping","Bills","Health","Fun","Education","Other")else listOf("Salary","Student","Gift","Freelance","Business","Refund","Investment","Other")
 Column(Modifier.fillMaxSize().padding(20.dp)){Top(if(out)"I Spent" else "I Earned",back);Label(if(out)"How much did you spend?" else "How much did you earn?");Field(amount,"${p.currency} 0"){amount=it};Label(if(out)"How did you pay?" else "Where did you add it?");Choices(method){method=it};Label(if(out)"Where did you spend?" else "Income source");Grid(cats,cat){cat=it};Label("Add a note (optional)");Field(note,"For example: Lunch with friends"){note=it};Spacer(Modifier.weight(1f));Button({save(Tx(UUID.randomUUID().toString(),type,amount.toDoubleOrNull()?:0.0,method,cat,note.trim(),System.currentTimeMillis()))},enabled=(amount.toDoubleOrNull()?:0.0)>0&&cat.isNotBlank(),modifier=Modifier.fillMaxWidth().height(54.dp),colors=ButtonDefaults.buttonColors(containerColor=Red),shape=RoundedCornerShape(15.dp)){Text(if(out)"Save expense" else "Save earnings",fontWeight=FontWeight.Bold)}}
}
@Composable private fun Choices(selected:String,pick:(String)->Unit){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(9.dp)){listOf("Cash","Online").forEach{o->val active=selected==o;Box(Modifier.weight(1f).height(50.dp).clip(RoundedCornerShape(13.dp)).background(if(active)RedSoft else Color.White).border(if(active)2.dp else 1.dp,if(active)Red else Line,RoundedCornerShape(13.dp)).clickable{pick(o)},contentAlignment=Alignment.Center){Text(if(o=="Cash")"💵 Cash" else "📱 Online",color=if(active)Red else Ink,fontWeight=if(active)FontWeight.Bold else FontWeight.Normal)}}}}
@Composable private fun Grid(cats:List<String>,selected:String,pick:(String)->Unit){val icons=mapOf("Food" to "🍔","Travel" to "🚕","Shopping" to "🛍️","Bills" to "🧾","Health" to "💊","Fun" to "🎬","Education" to "📚","Salary" to "💼","Student" to "🎓","Gift" to "🎁","Freelance" to "💻","Business" to "🏪","Refund" to "↩","Investment" to "📈","Other" to "•••");Column(verticalArrangement=Arrangement.spacedBy(7.dp)){cats.chunked(4).forEach{row->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(7.dp)){row.forEach{i->val active=selected==i;Column(Modifier.weight(1f).height(62.dp).clip(RoundedCornerShape(12.dp)).background(if(active)RedSoft else Color.White).border(if(active)2.dp else 1.dp,if(active)Red else Line,RoundedCornerShape(12.dp)).clickable{pick(i)},horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){Text(icons[i]?:"•",fontSize=16.sp);Text(i,fontSize=9.sp,fontWeight=if(active)FontWeight.Bold else FontWeight.Normal,maxLines=1)}};repeat(4-row.size){Spacer(Modifier.weight(1f))}}}}}

@Composable private fun History(p:Profile,txs:List<Tx>,back:()->Unit,del:(String)->Unit){var filter by rememberSaveable{mutableStateOf("All")};val shown=txs.filter{filter=="All"||it.method==filter};Column(Modifier.fillMaxSize().padding(20.dp)){Top("History",back);Spacer(Modifier.height(8.dp));Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){listOf("All","Cash","Online").forEach{f->Chip(f,f==filter){filter=f}}};Spacer(Modifier.height(16.dp));if(shown.isEmpty())Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){Text("No entries yet.\nAdd your first expense from Home.",textAlign=TextAlign.Center,color=Color(0xFF706D79))}else LazyColumn(verticalArrangement=Arrangement.spacedBy(9.dp)){items(shown,key={it.id}){t->TxRow(p,t,del)}}}}
@Composable private fun Reports(p:Profile,txs:List<Tx>,back:()->Unit){val spent=txs.filter{it.type=="spent"}.sumOf{it.amount};val earned=txs.filter{it.type=="earned"}.sumOf{it.amount};val top=txs.filter{it.type=="spent"}.groupBy{it.category}.maxByOrNull{it.value.sumOf{x->x.amount}}?.key?:"No spending yet";Column(Modifier.fillMaxSize().padding(20.dp)){Top("Reports",back);Spacer(Modifier.height(12.dp));Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){Stat("You earned",money(p.currency,earned),Green,Modifier.weight(1f));Stat("You spent",money(p.currency,spent),Red,Modifier.weight(1f))};Spacer(Modifier.height(17.dp));Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=Radius){Column(Modifier.padding(16.dp)){Text("Daily spending",fontWeight=FontWeight.Bold);Spacer(Modifier.height(14.dp));Text("Your daily graph will appear as you add expenses.",color=Color(0xFF706D79),fontSize=13.sp)}};Spacer(Modifier.height(16.dp));Card(colors=CardDefaults.cardColors(containerColor=RedSoft),shape=Radius){Column(Modifier.padding(16.dp)){Text("Your top expense",color=Red,fontSize=12.sp,fontWeight=FontWeight.Bold);Text(top,color=Ink,fontWeight=FontWeight.Bold,fontSize=20.sp)}}}}
@Composable private fun ProfilePage(p:Profile,back:()->Unit){Column(Modifier.fillMaxSize().padding(20.dp)){Top("Profile",back);Spacer(Modifier.height(15.dp));Row(verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(53.dp).background(Red,CircleShape),contentAlignment=Alignment.Center){Text(p.name.take(1).uppercase(),color=Color.White,fontWeight=FontWeight.Bold,fontSize=21.sp)};Spacer(Modifier.width(12.dp));Column{Text(p.name,fontWeight=FontWeight.Bold,fontSize=17.sp);Text(p.mobile,color=Color(0xFF706D79),fontSize=13.sp)}};Spacer(Modifier.height(27.dp));listOf("Currency · Indian Rupee ₹","Language · English","Cloud backup · Coming soon","Fingerprint lock · Coming soon","Private Notes · Coming soon").forEach{v->Card(Modifier.fillMaxWidth().padding(vertical=4.dp),colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(14.dp)){Row(Modifier.padding(16.dp),horizontalArrangement=Arrangement.SpaceBetween){Text(v,fontSize=13.sp);Text("›",color=Color(0xFF706D79))}}}}}

@Composable private fun Top(title:String,back:()->Unit){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){IconButton(back,Modifier.background(Color(0xFFEAE7EA),CircleShape)){Icon(Icons.Outlined.ArrowBack,"Back")};Text(title,Modifier.weight(1f),textAlign=TextAlign.Center,color=Ink,fontSize=21.sp,fontWeight=FontWeight.Bold);Spacer(Modifier.size(48.dp))}}
@Composable private fun Chip(text:String,selected:Boolean,click:()->Unit){Text(text,Modifier.clip(RoundedCornerShape(20.dp)).background(if(selected)Red else Color(0xFFEAE7EA)).clickable{click()}.padding(horizontal=14.dp,vertical=8.dp),color=if(selected)Color.White else Color(0xFF706D79),fontSize=12.sp,fontWeight=FontWeight.Bold)}
@Composable private fun TxRow(p:Profile,t:Tx,del:(String)->Unit){Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=Radius){Row(Modifier.fillMaxWidth().padding(12.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(39.dp).background(if(t.type=="spent")RedSoft else Color(0xFFFFF3CE),RoundedCornerShape(12.dp)),contentAlignment=Alignment.Center){Text(if(t.type=="spent")"↓" else "↑",color=if(t.type=="spent")Red else Color(0xFF9B6800),fontSize=20.sp,fontWeight=FontWeight.Bold)};Spacer(Modifier.width(10.dp));Column(Modifier.weight(1f)){Text(t.category+if(t.note.isNotBlank())" · ${t.note}" else "",fontWeight=FontWeight.Bold,fontSize=13.sp);Text("${t.method} · ${SimpleDateFormat("d MMM",Locale.US).format(Date(t.date))}",color=Color(0xFF706D79),fontSize=11.sp)};Text((if(t.type=="spent")"−" else "+")+money(p.currency,t.amount),color=if(t.type=="spent")Red else Green,fontWeight=FontWeight.Bold,fontSize=13.sp);IconButton({del(t.id)}){Icon(Icons.Outlined.Delete,"Delete entry",tint=Color(0xFF9A969D),modifier=Modifier.size(18.dp))}}}}
@Composable private fun Stat(label:String,value:String,color:Color,mod:Modifier){Card(mod,colors=CardDefaults.cardColors(containerColor=Color.White),shape=Radius){Column(Modifier.padding(14.dp)){Text(label,color=Color(0xFF706D79),fontSize=11.sp);Text(value,color=color,fontWeight=FontWeight.Bold,fontSize=19.sp)}}}
@Composable private fun Action(title:String,sub:String,icon:ImageVector,bg:Color,tint:Color,click:()->Unit){Card(Modifier.fillMaxWidth().clickable{click()},colors=CardDefaults.cardColors(containerColor=Color.White),shape=Radius){Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(41.dp).background(bg,RoundedCornerShape(13.dp)),contentAlignment=Alignment.Center){Icon(icon,null,tint=tint)};Spacer(Modifier.width(12.dp));Column(Modifier.weight(1f)){Text(title,fontWeight=FontWeight.Bold);Text(sub,color=Color(0xFF706D79),fontSize=11.sp)};Text("›",color=Color(0xFF706D79),fontSize=24.sp)}}}
@Composable private fun Bottom(selected:String,nav:(String)->Unit){Row(Modifier.fillMaxWidth().padding(top=12.dp),horizontalArrangement=Arrangement.SpaceAround){Nav("home","Home",Icons.Outlined.Home,selected,nav);Nav("history","History",Icons.Outlined.History,selected,nav);Nav("reports","Reports",Icons.Outlined.BarChart,selected,nav);Nav("profile","Profile",Icons.Outlined.Person,selected,nav)}}
@Composable private fun Nav(key:String,label:String,icon:ImageVector,selected:String,nav:(String)->Unit){val a=key==selected;Column(Modifier.clickable{nav(key)},horizontalAlignment=Alignment.CenterHorizontally){Icon(icon,label,tint=if(a)Red else Color(0xFF8A858D));Text(label,color=if(a)Red else Color(0xFF8A858D),fontSize=10.sp,fontWeight=if(a)FontWeight.Bold else FontWeight.Normal)}}
@Composable private fun Logo(small:Boolean=false){Box(Modifier.size(if(small)42.dp else 62.dp).background(Red,RoundedCornerShape(if(small)14.dp else 20.dp)),contentAlignment=Alignment.Center){Text("⌁",color=Color.White,fontSize=if(small)25.sp else 35.sp,fontWeight=FontWeight.Bold)}}
private fun money(c:String,v:Double)=c+NumberFormat.getNumberInstance(Locale.US).apply{maximumFractionDigits=0}.format(v)

private class Store(c:Context){private val p=c.getSharedPreferences("pocketpulse",Context.MODE_PRIVATE)
 fun profile():Profile?{val raw=p.getString("profile",null)?:return null;return try{val j=JSONObject(raw);Profile(j.getString("name"),j.getString("mobile"),j.getString("currency"),j.getString("language"),j.getDouble("cash"),j.getDouble("online"))}catch(_:Exception){null}}
 fun saveProfile(x:Profile){p.edit().putString("profile",JSONObject().put("name",x.name).put("mobile",x.mobile).put("currency",x.currency).put("language",x.language).put("cash",x.cash).put("online",x.online).toString()).apply()}
 fun txs():List<Tx>{val raw=p.getString("txs","[]")?:"[]";return try{val a=JSONArray(raw);(0 until a.length()).map{i->a.getJSONObject(i).let{j->Tx(j.getString("id"),j.getString("type"),j.getDouble("amount"),j.getString("method"),j.getString("category"),j.getString("note"),j.getLong("date"))}}}catch(_:Exception){emptyList()}}
 fun saveTxs(xs:List<Tx>){val a=JSONArray();xs.forEach{x->a.put(JSONObject().put("id",x.id).put("type",x.type).put("amount",x.amount).put("method",x.method).put("category",x.category).put("note",x.note).put("date",x.date))};p.edit().putString("txs",a.toString()).apply()}
 fun deleteTx(id:String,xs:List<Tx>){saveTxs(xs)}
}
