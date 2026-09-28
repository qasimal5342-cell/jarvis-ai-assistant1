const $=s=>document.querySelector(s);
const statusEl=$("#status"), heard=$("#heard"), orb=$("#orb"), logEl=$("#log");
const SpeechRecognition=window.SpeechRecognition||window.webkitSpeechRecognition;

function speak(text){
  if("speechSynthesis" in window){
    speechSynthesis.cancel();
    const u=new SpeechSynthesisUtterance(text);
    u.rate=.96; u.pitch=.95;
    speechSynthesis.speak(u);
  }
}
function log(who,text){
  const d=document.createElement("div");
  d.className="entry "+(who==="You"?"you":"jarvis");
  d.textContent=who+": "+text;
  logEl.prepend(d);
}
function reply(text){statusEl.textContent=text;log("JARVIS",text);speak(text)}
function calc(expr){
  let s=expr.toLowerCase()
    .replace(/what is|calculate|compute/g,"")
    .replace(/times|multiplied by/g,"*")
    .replace(/plus/g,"+").replace(/minus/g,"-")
    .replace(/divided by|over/g,"/")
    .replace(/[^0-9+*/().\- ]/g,"").trim();
  if(!s)return null;
  try{
    const v=Function('"use strict";return ('+s+')')();
    return Number.isFinite(v)?v:null;
  }catch{return null}
}
function command(raw){
  const c=raw.trim(), x=c.toLowerCase();
  if(!c)return;
  heard.textContent="Heard: "+c;
  log("You",c);

  if(/^(hi|hello|hey|assalam|salam)/.test(x))
    return reply("Hello. I am JARVIS. How can I help you?");
  if(x==="help"||x.includes("what can you do"))
    return reply("You can ask for time, date, calculations, web searches, websites, or notes. Say open YouTube, search Google for something, save note, or show notes.");
  if(x.includes("time"))
    return reply("The time is "+new Date().toLocaleTimeString([], {hour:"numeric",minute:"2-digit"}));
  if(x.includes("date")||x.includes("day"))
    return reply("Today is "+new Date().toLocaleDateString([], {weekday:"long",year:"numeric",month:"long",day:"numeric"}));

  const cv=calc(x);
  if(cv!==null)return reply("The answer is "+cv);

  if(x.startsWith("save note ")){
    const n=c.slice(10).trim();
    const a=JSON.parse(localStorage.getItem("jarvis_notes")||"[]");
    a.push({text:n,time:new Date().toISOString()});
    localStorage.setItem("jarvis_notes",JSON.stringify(a));
    return reply("Note saved.");
  }
  if(x==="show notes"||x==="read notes"){
    const a=JSON.parse(localStorage.getItem("jarvis_notes")||"[]");
    return reply(a.length?a.map((n,i)=>(i+1)+": "+n.text).join(". "):"You have no saved notes.");
  }
  if(x==="clear notes"){
    localStorage.removeItem("jarvis_notes");
    return reply("All notes cleared.");
  }
  if(x==="clear history"){
    logEl.innerHTML="";
    return reply("Command history cleared.");
  }

  let q=null;
  if(x.startsWith("search google for "))q=c.slice(18).trim();
  if(q){
    window.open("https://www.google.com/search?q="+encodeURIComponent(q),"_blank");
    return reply("Searching Google for "+q);
  }

  const sites={
    youtube:"https://www.youtube.com",google:"https://www.google.com",
    github:"https://github.com",facebook:"https://www.facebook.com",
    whatsapp:"https://web.whatsapp.com",instagram:"https://www.instagram.com",
    gmail:"https://mail.google.com",bilibili:"https://www.bilibili.com"
  };
  if(x.startsWith("open ")){
    const target=x.slice(5).trim();
    if(sites[target]){
      window.open(sites[target],"_blank");
      return reply("Opening "+target+".");
    }
    window.open("https://www.google.com/search?q="+encodeURIComponent(target),"_blank");
    return reply("Searching for "+target);
  }

  reply("I understood the command, but I do not have an action for it yet. Say Help to see supported commands.");
}

function startVoice(){
  if(!SpeechRecognition)
    return reply("Voice recognition is not supported in this browser. Try Chrome.");
  const recognition=new SpeechRecognition();
  recognition.lang="en-US";
  recognition.interimResults=false;
  recognition.maxAlternatives=1;
  recognition.onstart=()=>{orb.classList.add("listening");statusEl.textContent="Listening..."};
  recognition.onresult=e=>command(e.results[0][0].transcript);
  recognition.onerror=e=>reply("Voice error: "+e.error);
  recognition.onend=()=>orb.classList.remove("listening");
  recognition.start();
}

$("#mic").onclick=startVoice;
$("#send").onclick=()=>{command($("#input").value);$("#input").value=""};
$("#input").addEventListener("keydown",e=>{if(e.key==="Enter")$("#send").click()});
document.querySelectorAll("[data-cmd]").forEach(b=>b.onclick=()=>command(b.dataset.cmd));
$("#clearLog").onclick=()=>logEl.innerHTML="";
