package com.bechir.system

import android.os.Bundle
import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.clip
import androidx.core.graphics.drawable.toBitmap

class UltraActivity : ComponentActivity() {
 data class Entry(val label:String,val intent:Intent,val bitmap:Bitmap)
 override fun onCreate(savedInstanceState:Bundle?) {
 super.onCreate(savedInstanceState)
 val prefs=getSharedPreferences("infinity2",0)
 val apps=packageManager.queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),0).filter{it.activityInfo.packageName!=packageName}.map {
 Entry(it.loadLabel(packageManager).toString(),Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setClassName(it.activityInfo.packageName,it.activityInfo.name).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),it.loadIcon(packageManager).toBitmap(96,96))
 }.sortedBy{it.label.lowercase()}
 setContent {
 var page by remember { mutableIntStateOf(0) }
 var columns by remember { mutableIntStateOf(prefs.getInt("columns",3)) }
 var size by remember { mutableFloatStateOf(prefs.getFloat("size",52f)) }
 var radius by remember { mutableFloatStateOf(prefs.getFloat("radius",24f)) }
 var spacing by remember { mutableFloatStateOf(prefs.getFloat("spacing",10f)) }
 var names by remember { mutableStateOf(prefs.getBoolean("names",true)) }
 var accentIndex by remember { mutableIntStateOf(prefs.getInt("accent",0)) }
 var query by remember { mutableStateOf("") }
 val palette=listOf(Color(0xFF80E6FF),Color(0xFFB6A0FF),Color(0xFFFFC477),Color(0xFF85E7B3),Color(0xFFFF8DAE))
 val accent=palette[accentIndex.coerceIn(0,4)]
 LaunchedEffect(columns,size,radius,spacing,names,accentIndex){prefs.edit().putInt("columns",columns).putFloat("size",size).putFloat("radius",radius).putFloat("spacing",spacing).putBoolean("names",names).putInt("accent",accentIndex).apply()}
 MaterialTheme(colorScheme=darkColorScheme(primary=accent,background=Color(0xFF0B101B),surface=Color(0xFF182235))) {
 Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF17243A),Color(0xFF080D16)))).statusBarsPadding().navigationBarsPadding().padding(18.dp)) {
 Text("BECHIR / ULTRA",color=accent,fontSize=13.sp,fontWeight=FontWeight.Bold)
 Text(if(page==2)"Design Studio" else if(page==1)"التطبيقات" else "مساحتك الخاصة",fontSize=30.sp,color=Color.White,fontWeight=FontWeight.Bold)
 Spacer(Modifier.height(16.dp))
 if(page==2){
 Column(Modifier.weight(1f).verticalScroll(rememberScrollState())){
 Text("معاينة مباشرة",color=accent)
 Row(Modifier.fillMaxWidth().padding(vertical=12.dp),horizontalArrangement=Arrangement.spacedBy(spacing.dp)){apps.take(columns).forEach{a->Tile(a,Modifier.weight(1f),size,radius,names,accent)}}
 Text("لون الهوية")
 Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){palette.forEachIndexed{i,c->Box(Modifier.size(42.dp).clip(RoundedCornerShape(16.dp)).background(c).clickable{accentIndex=i})}}
 Text("عدد الأعمدة: "+columns)
 Slider(value=columns.toFloat(),onValueChange={columns=it.toInt()},valueRange=2f..5f,steps=2)
 Text("حجم الأيقونة");Slider(value=size,onValueChange={size=it},valueRange=32f..76f)
 Text("استدارة البطاقة");Slider(value=radius,onValueChange={radius=it},valueRange=0f..40f)
 Text("المسافات");Slider(value=spacing,onValueChange={spacing=it},valueRange=2f..18f)
 Row(verticalAlignment=Alignment.CenterVertically){Text("إظهار الأسماء",Modifier.weight(1f));Switch(names,{names=it})}
 OutlinedButton(onClick={startActivity(Intent(this@UltraActivity,MainActivity::class.java))}){Text("فتح الواجهة الكلاسيكية")}
 }
 }else{
 if(page==0){var now by remember{mutableStateOf("")};LaunchedEffect(Unit){while(true){now=java.text.SimpleDateFormat("HH:mm",java.util.Locale.getDefault()).format(java.util.Date());kotlinx.coroutines.delay(1000)}};Text(now,fontSize=64.sp,color=accent,fontWeight=FontWeight.Light);Text(java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL).format(java.util.Date()),color=Color.White);Spacer(Modifier.height(16.dp))}
 if(page==1)OutlinedTextField(query,{query=it},Modifier.fillMaxWidth(),placeholder={Text("ابحث في التطبيقات")},singleLine=true)
 LazyVerticalGrid(columns=GridCells.Fixed(columns),modifier=Modifier.weight(1f),horizontalArrangement=Arrangement.spacedBy(spacing.dp),verticalArrangement=Arrangement.spacedBy(spacing.dp),contentPadding=PaddingValues(vertical=12.dp)){items(if(page==0)apps.take(12) else apps.filter{it.label.contains(query,true)}){a->Tile(a,Modifier,size,radius,names,accent)}}
 }
 Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)){listOf("الرئيسية","التطبيقات","التصميم").forEachIndexed{i,label->if(page==i)Button(onClick={page=i},modifier=Modifier.weight(1f),contentPadding=PaddingValues(4.dp)){Text(label)}else OutlinedButton(onClick={page=i},modifier=Modifier.weight(1f),contentPadding=PaddingValues(4.dp)){Text(label)}}}
 }
 }
 }
 }
 @Composable fun Tile(a:Entry,modifier:Modifier,size:Float,radius:Float,names:Boolean,accent:Color){
 Column(modifier.clip(RoundedCornerShape(radius.dp)).background(Color(0xFF202D40)).clickable{try{startActivity(a.intent)}catch(_:Exception){}}.padding(8.dp),horizontalAlignment=Alignment.CenterHorizontally){Image(a.bitmap.asImageBitmap(),a.label,Modifier.size(size.dp));if(names)Text(a.label,color=Color.White,fontSize=11.sp,maxLines=1,overflow=TextOverflow.Ellipsis)}
 }
}
