const API = 'http://localhost:8080';
let token = null, currentUser = null, allTasks = [], editingTaskId = null, currentFilter = 'all';

// ---------- AUTH ----------
function switchTab(tab) {
  document.getElementById('tabLogin').classList.toggle('active', tab==='login');
  document.getElementById('tabRegister').classList.toggle('active', tab==='register');
  document.getElementById('loginForm').style.display = tab==='login' ? 'block':'none';
  document.getElementById('registerForm').style.display = tab==='register' ? 'block':'none';
  hideMsg();
}
function showMsg(text, type) { const m=document.getElementById('authMsg'); m.textContent=text; m.className='auth-msg '+type; }
function hideMsg() { document.getElementById('authMsg').className='auth-msg'; }

async function register() {
  const nom=document.getElementById('regNom').value.trim();
  const email=document.getElementById('regEmail').value.trim();
  const password=document.getElementById('regPassword').value;
  if(!nom||!email||!password){ showMsg('Veuillez remplir tous les champs','error'); return; }
  try {
    const res=await fetch(API+'/users/register',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({nom,email,password})});
    const data=await res.json();
    if(res.ok){ token=data.token; currentUser=data.user; showMsg('Compte créé avec succès !','success'); setTimeout(enterApp,600); }
    else showMsg(data.error||'Erreur lors de l\'inscription','error');
  } catch(e){ showMsg('Impossible de contacter le serveur','error'); }
}

async function login() {
  const email=document.getElementById('loginEmail').value.trim();
  const password=document.getElementById('loginPassword').value;
  if(!email||!password){ showMsg('Veuillez remplir tous les champs','error'); return; }
  try {
    const res=await fetch(API+'/users/login',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({email,password})});
    const data=await res.json();
    if(res.ok){ token=data.token; currentUser=data.user; enterApp(); }
    else showMsg(data.error||'Email ou mot de passe incorrect','error');
  } catch(e){ showMsg('Impossible de contacter le serveur','error'); }
}

function logout() { token=null; currentUser=null; allTasks=[]; document.getElementById('appScreen').classList.remove('active'); document.getElementById('authScreen').style.display='flex'; document.getElementById('loginEmail').value=''; document.getElementById('loginPassword').value=''; }

function enterApp() {
  document.getElementById('authScreen').style.display='none';
  document.getElementById('appScreen').classList.add('active');
  document.getElementById('userName').textContent=currentUser.nom;
  document.getElementById('userMail').textContent=currentUser.email;
  document.getElementById('userAvatar').textContent=currentUser.nom.charAt(0).toUpperCase();
  const today=new Date();
  document.getElementById('todayDate').textContent=today.toLocaleDateString('fr-FR',{weekday:'long',day:'numeric',month:'long',year:'numeric'});
  document.getElementById('taskDate').valueAsDate=new Date(Date.now()+86400000*3);
  loadTasks();
}

// ---------- NAV ----------
function showPage(page, el) {
  document.querySelectorAll('.page').forEach(p=>p.classList.remove('active'));
  document.getElementById('page-'+page).classList.add('active');
  document.querySelectorAll('.nav-item').forEach(n=>n.classList.remove('active'));
  el.classList.add('active');
  if(page==='stats') renderStats();
}

// ---------- TASKS ----------
async function loadTasks() {
  try {
    const res=await fetch(API+'/tasks',{headers:{'Authorization':'Bearer '+token}});
    const data=await res.json();
    allTasks=data.tasks||[];
    renderAll();
  } catch(e){ showToast('Erreur de chargement'); }
}

function renderAll() { renderTasks(); renderDashboard(); }

function renderDashboard() {
  const total=allTasks.length;
  const urgent=allTasks.filter(t=>t.priorite==='HAUTE'&&t.statut!=='TERMINE').length;
  const done=allTasks.filter(t=>t.statut==='TERMINE').length;
  const progress=allTasks.filter(t=>t.statut==='EN_COURS').length;
  document.getElementById('statTotal').textContent=total;
  document.getElementById('statUrgent').textContent=urgent;
  document.getElementById('statDone').textContent=done;
  document.getElementById('statProgress').textContent=progress;
  const pct=total>0?Math.round(done/total*100):0;
  const bar=document.getElementById('globalProgress');
  setTimeout(()=>{ bar.style.width=pct+'%'; bar.textContent=pct+'%'; },100);
  const top=allTasks.filter(t=>t.statut!=='TERMINE').slice(0,4);
  document.getElementById('dashTaskList').innerHTML=top.length?top.map(taskHTML).join(''):emptyHTML('Aucune tâche en attente');
}

function renderTasks() {
  let tasks=allTasks;
  if(currentFilter!=='all') tasks=allTasks.filter(t=>t.statut===currentFilter);
  document.getElementById('taskList').innerHTML=tasks.length?tasks.map(taskHTML).join(''):emptyHTML('Aucune tâche ici');
}

function taskHTML(t) {
  const done=t.statut==='TERMINE';
  const dateStr=new Date(t.dateLimite).toLocaleDateString('fr-FR',{day:'numeric',month:'short'});
  const prioLabel={HAUTE:'Haute',MOYENNE:'Moyenne',BASSE:'Basse'}[t.priorite];
  return `<div class="task-card p-${t.priorite} ${done?'done':''}">
    <div class="task-check ${done?'done':''}" onclick="toggleDone(${t.id},'${t.statut}')">${done?'✓':''}</div>
    <div class="task-body">
      <div class="task-title">${esc(t.titre)}</div>
      <div class="task-desc">${esc(t.description||'Pas de description')}</div>
    </div>
    <div class="task-meta">
      <span class="badge ${t.priorite}">${prioLabel}</span>
      <span class="task-date">◷ ${dateStr}</span>
      <span class="task-score" title="Score de priorité">${t.scorePriorite}</span>
      <div class="task-actions">
        <button class="icon-btn" onclick="editTask(${t.id})">✎</button>
        <button class="icon-btn del" onclick="deleteTask(${t.id})">✕</button>
      </div>
    </div>
  </div>`;
}

function emptyHTML(msg){ return `<div class="empty"><div class="ico">☰</div><h3>${msg}</h3><p>Créez votre première tâche pour commencer</p></div>`; }
function esc(s){ return (s||'').replace(/[&<>"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c])); }

function filterTasks(f, el){ currentFilter=f; document.querySelectorAll('.chip').forEach(c=>c.classList.remove('active')); el.classList.add('active'); renderTasks(); }

// ---------- MODAL ----------
function openModal(){ editingTaskId=null; document.getElementById('modalTitle').textContent='Nouvelle tâche'; document.getElementById('modalSaveBtn').textContent='Créer la tâche'; document.getElementById('taskTitre').value=''; document.getElementById('taskDesc').value=''; document.getElementById('taskPriorite').value='MOYENNE'; document.getElementById('taskDate').valueAsDate=new Date(Date.now()+86400000*3); document.getElementById('statutField').style.display='none'; document.getElementById('taskModal').classList.add('active'); }
function closeModal(){ document.getElementById('taskModal').classList.remove('active'); }

function editTask(id){
  const t=allTasks.find(x=>x.id===id); if(!t) return;
  editingTaskId=id;
  document.getElementById('modalTitle').textContent='Modifier la tâche';
  document.getElementById('modalSaveBtn').textContent='Enregistrer';
  document.getElementById('taskTitre').value=t.titre;
  document.getElementById('taskDesc').value=t.description||'';
  document.getElementById('taskPriorite').value=t.priorite;
  document.getElementById('taskDate').value=t.dateLimite;
  document.getElementById('taskStatut').value=t.statut;
  document.getElementById('statutField').style.display='block';
  document.getElementById('taskModal').classList.add('active');
}

async function saveTask(){
  const titre=document.getElementById('taskTitre').value.trim();
  if(!titre){ showToast('Le titre est obligatoire'); return; }
  const body={ titre, description:document.getElementById('taskDesc').value.trim(), priorite:document.getElementById('taskPriorite').value, dateLimite:document.getElementById('taskDate').value };
  if(editingTaskId) body.statut=document.getElementById('taskStatut').value;
  try {
    let res;
    if(editingTaskId) res=await fetch(API+'/tasks/'+editingTaskId,{method:'PUT',headers:{'Content-Type':'application/json','Authorization':'Bearer '+token},body:JSON.stringify(body)});
    else res=await fetch(API+'/tasks',{method:'POST',headers:{'Content-Type':'application/json','Authorization':'Bearer '+token},body:JSON.stringify(body)});
    if(res.ok){ closeModal(); loadTasks(); showToast(editingTaskId?'Tâche modifiée ✓':'Tâche créée ✓'); }
    else showToast('Erreur lors de l\'enregistrement');
  } catch(e){ showToast('Erreur de connexion'); }
}

async function toggleDone(id, statut){
  const t=allTasks.find(x=>x.id===id); if(!t) return;
  const newStatut=statut==='TERMINE'?'A_FAIRE':'TERMINE';
  try {
    const res=await fetch(API+'/tasks/'+id,{method:'PUT',headers:{'Content-Type':'application/json','Authorization':'Bearer '+token},body:JSON.stringify({titre:t.titre,description:t.description,priorite:t.priorite,statut:newStatut,dateLimite:t.dateLimite})});
    if(res.ok) loadTasks();
  } catch(e){ showToast('Erreur'); }
}

async function deleteTask(id){
  if(!confirm('Supprimer cette tâche ?')) return;
  try {
    const res=await fetch(API+'/tasks/'+id,{method:'DELETE',headers:{'Authorization':'Bearer '+token}});
    if(res.ok){ loadTasks(); showToast('Tâche supprimée'); }
  } catch(e){ showToast('Erreur'); }
}

// ---------- STATS ----------
function renderStats(){
  const total=allTasks.length;
  const done=allTasks.filter(t=>t.statut==='TERMINE').length;
  const rate=total>0?Math.round(done/total*100):0;
  const avgScore=total>0?Math.round(allTasks.reduce((s,t)=>s+t.scorePriorite,0)/total):0;
  const today=new Date().toISOString().split('T')[0];
  const overdue=allTasks.filter(t=>t.statut!=='TERMINE'&&t.dateLimite<today).length;
  document.getElementById('statRate').textContent=rate+'%';
  document.getElementById('statAvgScore').textContent=avgScore;
  document.getElementById('statOverdue').textContent=overdue;
  document.getElementById('statWeek').textContent=total;
  const counts={HAUTE:0,MOYENNE:0,BASSE:0};
  allTasks.forEach(t=>counts[t.priorite]++);
  const colors={HAUTE:'#DC2626',MOYENNE:'#EA580C',BASSE:'#16A34A'};
  const labels={HAUTE:'Haute',MOYENNE:'Moyenne',BASSE:'Basse'};
  document.getElementById('priorityBars').innerHTML=['HAUTE','MOYENNE','BASSE'].map(p=>{
    const pct=total>0?Math.round(counts[p]/total*100):0;
    return `<div><div style="display:flex;justify-content:space-between;font-size:13.5px;font-weight:600;margin-bottom:6px"><span style="color:${colors[p]}">${labels[p]}</span><span style="color:var(--gray)">${counts[p]} tâche(s)</span></div><div class="progress-bar-bg" style="height:10px"><div style="height:100%;width:${pct}%;background:${colors[p]};border-radius:10px;transition:width 0.8s"></div></div></div>`;
  }).join('');
}

// ---------- TOAST ----------
let toastTimer;
function showToast(msg){ const t=document.getElementById('toast'); t.textContent=msg; t.classList.add('show'); clearTimeout(toastTimer); toastTimer=setTimeout(()=>t.classList.remove('show'),2800); }

// Enter key support
document.addEventListener('DOMContentLoaded',()=>{
  document.getElementById('loginPassword').addEventListener('keypress',e=>{if(e.key==='Enter')login();});
  document.getElementById('regPassword').addEventListener('keypress',e=>{if(e.key==='Enter')register();});
});
